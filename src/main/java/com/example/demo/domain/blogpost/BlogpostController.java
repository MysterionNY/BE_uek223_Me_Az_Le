package com.example.demo.domain.blogpost;

import com.example.demo.domain.blogpost.dto.BlogpostDTO;
import com.example.demo.domain.blogpost.dto.BlogpostMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/blogposts")
public class BlogpostController {   
    private final BlogpostService blogpostService;
    private final UserService userService;
    private final BlogpostMapper blogpostMapper;

    public BlogpostController(BlogpostService blogpostService, BlogpostMapper blogpostMapper, UserService userService) {
        this.blogpostService = blogpostService;
        this.userService = userService;
        this.blogpostMapper = blogpostMapper;
    }

    @Operation(summary = "Get all blogposts with Pagination", description = "Returns a list of all blogposts in the database with author and creation date." +
            " Pagination can be used to filter by the amount of entries that are shown and also which category. Available categories are: ADVICE, HEALTH, SPORT, FOOD, HISTORY")
    @GetMapping
    public ResponseEntity<List<BlogpostDTO>> findAllPaginated(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Blogpost> blogposts = blogpostService.findAllPaginated(category, PageRequest.of(page, size));
        List<BlogpostDTO> blogpostDTOS = blogposts.stream().map(blogpostMapper::toDTO).toList();
        return new ResponseEntity<>(blogpostDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Gets blogpost by ID", description = "Retrieves the blogpost via ID from database with author and creation date.")
    @GetMapping("/{blogpostId}")
    public ResponseEntity<BlogpostDTO> findById(@PathVariable UUID blogpostId) {
        Blogpost blogpost = blogpostService.findById(blogpostId);
        return new ResponseEntity<>(blogpostMapper.toDTO(blogpost), HttpStatus.OK);
    }

    @Operation(summary = "Get all blogposts of specific author", description = "Retrieves a list of all blogposts, via authorID, from the database from a specific author.")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BlogpostDTO>> findByAuthor(@PathVariable UUID authorId) {
        User blogpostAuthor = userService.findById(authorId);
        List<Blogpost> authorBlogposts = blogpostService.findBlogpostsByAuthor(blogpostAuthor);
        List<BlogpostDTO> authorBlogpostDTOS = authorBlogposts.stream().map(blogpostMapper::toDTO).toList();
        return new ResponseEntity<>(authorBlogpostDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Creates new blogpost", description = "Creates a new blogpost and saves it to the database. Only accessible by role User and Admin. Also checks for authority BLOGPOST_CREATE")
    @PostMapping
    @PreAuthorize("hasAuthority('BLOGPOST_CREATE')")
    public ResponseEntity<BlogpostDTO> createBlogpost(@Valid @RequestBody Blogpost blogpost) {
        Blogpost createdBlogpost = blogpostService.createBlogpost(blogpost);
        return new ResponseEntity<>(blogpostMapper.toDTO(createdBlogpost), HttpStatus.CREATED);
    }

    @Operation(summary = "Updates blogpost", description = "Updates a blogpost, via ID, with the new data, saving it in the database. Only accessible by role User and Admin. Also checks for authority BLOGPOST_UPDATE")
    @PutMapping("/{blogpostId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BLOGPOST_UPDATE')")
    public ResponseEntity<BlogpostDTO> updateBlogpost(@Valid @PathVariable UUID blogpostId, @RequestBody @Valid BlogpostDTO blogpostDTO) {
        Blogpost updatedBlogpost = blogpostService.updateBlogpost(blogpostId, blogpostDTO);
        return new ResponseEntity<>(blogpostMapper.toDTO(updatedBlogpost), HttpStatus.OK);
    }

    @Operation(summary = "Deletes blogpost", description = "Deletes blogpost, via ID and removes it from the database. Only accessible by role User and Admin. Also checks for authority BLOGPOST_DELETE")
    @DeleteMapping("/{blogpostId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BLOGPOST_DELETE')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID blogpostId) {
        blogpostService.deleteById(blogpostId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}