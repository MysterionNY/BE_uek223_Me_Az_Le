package com.example.demo.domain.blogpost;

import com.example.demo.core.exception.InvalidCategoryException;
import com.example.demo.core.generic.AbstractServiceImpl;
import com.example.demo.domain.blogpost.dto.BlogpostDTO;
import com.example.demo.domain.user.User;
import com.example.demo.domain.role.Role;
import com.example.demo.domain.user.UserDetailsImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class BlogpostServiceImpl extends AbstractServiceImpl<Blogpost> implements BlogpostService {
    private final BlogpostRepository blogpostRepository;
    private static final String NOT_FOUND_BLOGPOST = "Blogpost not found with id: ";

    public BlogpostServiceImpl(BlogpostRepository blogpostRepository) {
        super(blogpostRepository);
        this.blogpostRepository = blogpostRepository;
    }

    public Page<Blogpost> findAllPaginated(String category, Pageable pageable) {
        Page<Blogpost> blogpostPage;

        if (category != null && !category.isBlank()) {
            final BlogpostCategoryEnum catEnum;
            try {
                catEnum = BlogpostCategoryEnum.valueOf(category.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCategoryException("Diese Kategorie ist nicht verfügbar: " + category);
            }
            blogpostPage = blogpostRepository.findByCategory(catEnum, pageable);
        } else {
            blogpostPage = blogpostRepository.findAll(pageable);
        }

        if (blogpostPage.isEmpty()) {
            log.warn("No blogposts found in the database (category={})", category);
        } else {
            log.info("{} blogposts retrieved from database (category={})",
                    blogpostPage.getNumberOfElements(), category);
        }
        return blogpostPage;
    }


    public Blogpost findById(UUID id){
        return blogpostRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Blogpost with ID {} not found.", id);
                    return new RuntimeException("Blogpost not found");
                });
    }

    public List<Blogpost> findBlogpostsByAuthor(User author) {
        List<Blogpost> posts = blogpostRepository.findByAuthor(author);
        if (posts.isEmpty()){
            log.warn("No blogposts found for author with ID {}", author.getId());
        } else {
            log.info("{} blogposts found for author with ID {}", posts.size(), author.getId());
        }
        return posts;
    }

    public Blogpost createBlogpost(Blogpost newBlogpost){
        if(newBlogpost == null ||
                newBlogpost.getTitle() == null ||
                newBlogpost.getText() == null ||
                newBlogpost.getCategory() == null
        ){
            throw new IllegalArgumentException("Invalid blogpost data: title, text and category cannot be null.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userDetails.user();
        newBlogpost.setAuthor(currentUser);
        newBlogpost.setCreatedAt(LocalDateTime.now());
        log.info("Created new Blogpost: " + newBlogpost.getTitle());
        return blogpostRepository.save(newBlogpost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blogpost updateBlogpost(UUID id, BlogpostDTO blogpostDTO)
            throws NoSuchElementException, IllegalArgumentException {
        Blogpost existingBlogpost = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Blogpost with id {} not found in repository", id);
                    return new NoSuchElementException(
                            String.format("Blogpost with id: '%s' was not found", id));
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userDetails.user();
        boolean isAdmin = currentUser.getRoles().stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .anyMatch(n -> n.equalsIgnoreCase("ADMIN"));

        boolean isOwner = Objects.equals(existingBlogpost.getAuthor().getId(), currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to update this blogpost");
        }

        log.info("Updating blogpost with id: {}", id);
        if (blogpostDTO == null) {
            log.error("Blogpost with id {} is null -> cannot update", id);
            throw new IllegalArgumentException(String.format("The given blogpost with id: '%s' cannot be null", id));
        }

        log.debug("Found blogpost with id {} -> updating fields", id);
        existingBlogpost.setTitle(blogpostDTO.getTitle());
        existingBlogpost.setText(blogpostDTO.getText());
        log.info("Successfully updated blogpost with id: {}", id);
        return repository.save(existingBlogpost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userDetails.user();
        log.info("Deleting blogpost with id: {}", id);
        Blogpost existingBlogpost = blogpostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_BLOGPOST + id));
        boolean isAdmin = currentUser.getRoles().stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .anyMatch(n -> n.equalsIgnoreCase("ADMIN"));
        boolean isOwner = Objects.equals(existingBlogpost.getAuthor().getId(), currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this blogpost");
        }
        blogpostRepository.delete(existingBlogpost);
    }
}
