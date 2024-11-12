package com.spring.blog_jwt.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.spring.blog_jwt.entities.Blog;
import com.spring.blog_jwt.entities.Datatable;


public interface BlogService {

	void deleteBlogById(long id);

	Blog publishBlogById(long id);

	Blog getBlogById(long id);

	Blog editBlogById(Blog blog, long id);

	ResponseEntity<Map<String, Object>> getAllBlogs(Datatable datatable);

	Blog createBlog(Blog blog);

	List<Blog> getRecentBlogs();

	List<Blog> findAllBlogs();

}
