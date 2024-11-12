package com.spring.blog_jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.blog_jwt.entities.Blog;
import com.spring.blog_jwt.entities.Datatable;
import com.spring.blog_jwt.services.BlogService;



@RestController
@CrossOrigin("*")
public class DatatableORM {

	@Autowired
	private BlogService blogService;
	
	@PostMapping("/blogs/post")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {

		System.out.println("Submitting blog: " + blog);

		try {
//	        Blog savedBlog = blogRepository.save(blog);
			Blog savedBlog = blogService.createBlog(blog);

			return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@PutMapping("/blogs/edit/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Blog> editblogById(@RequestBody Blog blog, @PathVariable long id) {

		try {
			Blog blog1 = blogService.editBlogById(blog, id);

			return ResponseEntity.ok(blog1);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}

	@PostMapping("/blogs/publish/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Blog> publishBlog(@PathVariable long id) {

		try {
			Blog publishedBlog = blogService.publishBlogById(id);
			return ResponseEntity.ok(publishedBlog);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@DeleteMapping("/blogs/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> deleteBlogById(@PathVariable long id) {

		try {
			blogService.deleteBlogById(id);
			return ResponseEntity.ok("Blog deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found.");
		}

	}

	@PostMapping("/blogs/allx")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllBlogs(@RequestBody Datatable datatable) {

		System.out.println("xx-->");
		return blogService.getAllBlogs(datatable);
	}

	
	@GetMapping("/view/articles")
	public List<Blog> getAllPublishedBlog() {
		return blogService.findAllBlogs();
	}

}