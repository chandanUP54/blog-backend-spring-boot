package com.spring.blog_jwt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.spring.blog_jwt.entities.Blog;
import com.spring.blog_jwt.entities.Comment;
import com.spring.blog_jwt.exception.UserException;
import com.spring.blog_jwt.services.AuthenticationService;
import com.spring.blog_jwt.services.BlogComment;
import com.spring.blog_jwt.services.BlogService;

@RestController
public class BlogDetails {

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogComment blogComment;

	@Autowired
	private AuthenticationService authenticationService;

	// use templating here
	@GetMapping("/blog/view/{id}")
	public ResponseEntity<Map<String, Object>> blogDetailById(@PathVariable long id) {

		Blog blog = blogService.getBlogById(id);
		List<Comment> comments = blogComment.findAllCommentForABlog(id);

		if (blog.getPublishedAt() != null) {
			Map<String, Object> response = new HashMap<>();
			response.put("blog", blog);

			response.put("comments", comments);

			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

	}

	@PostMapping("/blog/{blogId}/comment")
	public ResponseEntity<Comment> postingComment(@RequestHeader("Authorization") String jwt,
			@RequestBody Comment commentRequest, @PathVariable long blogId) {
		jwt = jwt.substring(7);
		try {

			Comment comment = new Comment();
			comment.setComment(commentRequest.getComment());

			Comment savedComment = blogComment.createComment(comment, blogId, jwt);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
		} catch (Exception e) {
			e.printStackTrace(); // Print the stack trace for debugging
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@DeleteMapping("/blog/{blogID}/comment/{commentID}")
	public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String jwt, @PathVariable long blogID,
			@PathVariable long commentID) {

		jwt = jwt.substring(7);

		try {
			blogComment.deleteComment(blogID, commentID, jwt);
			return ResponseEntity.ok("Comment Deleted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("COMMENT NOT DELETED ");
		}

	}

	@PutMapping("/blog/{blogID}/update/{commentID}")
	public ResponseEntity<Comment> editComment(@RequestHeader("Authorization") String jwt,
			@RequestBody Comment commentReq, @PathVariable long blogID, @PathVariable long commentID)
			throws UserException {

		jwt = jwt.substring(7);
		Comment comment = blogComment.updateComment(commentReq, commentID, blogID, jwt);
		if (comment != null) {
			return ResponseEntity.status(HttpStatus.OK).body(comment);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}

	@GetMapping("/recentBlogs")
	public List<Blog> showHomePage() {

		List<Blog> recentBlogs = blogService.getRecentBlogs();

		return recentBlogs; // View name for home page
	}
}
