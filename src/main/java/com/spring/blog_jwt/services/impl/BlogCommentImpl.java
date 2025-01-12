package com.spring.blog_jwt.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.spring.blog_jwt.entities.Blog;
import com.spring.blog_jwt.entities.Comment;
import com.spring.blog_jwt.entities.Users;
import com.spring.blog_jwt.exception.UserException;
import com.spring.blog_jwt.repository.BlogRepository;
import com.spring.blog_jwt.repository.CommentRepository;
import com.spring.blog_jwt.services.AuthenticationService;
import com.spring.blog_jwt.services.BlogComment;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BlogCommentImpl implements BlogComment {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public Comment createComment(Comment comment, long blogId, String jwt) {
		Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new EntityNotFoundException("Blog not found"));

		Comment comment2 = new Comment();
		try {
			Users users = authenticationService.findUserUsingJwt(jwt);
			comment2.setBlog(blog);
			comment2.setComment(comment.getComment());
			comment2.setUsers(users);
			comment2 = commentRepository.save(comment2);
			System.out.println("user" + users);
		} catch (UserException e) {
			e.printStackTrace();
		}

		return comment2;
	}

	@Override
	public List<Comment> findAllCommentForABlog(long id) {
		List<Comment> comments = commentRepository.findByBlogId(id);
		return comments;
	}

	@Override
	public ResponseEntity<?> deleteComment(long blogID, long commentID, String jwt) throws UserException {

		Users user = authenticationService.findUserUsingJwt(jwt);

		Comment comment = commentRepository.findById(commentID).get();
		System.out.println("x" + user.getId() + " " + comment);
		if (user.getId() == comment.getUsers().getId()) {
			System.out.println("y" + user.getId() + " " + comment.getUsers().getId());
			commentRepository.delete(comment);
			return ResponseEntity.ok(null);
		}
		return (ResponseEntity<?>) ResponseEntity.notFound();

	}

	@Override
	public Comment updateComment(Comment commentReq, long commentID, long blogID, String jwt) throws UserException {

		Users user = authenticationService.findUserUsingJwt(jwt);

		Comment comment = commentRepository.findById(commentID)
				.orElseThrow(() -> new EntityNotFoundException("Comment not found"));

		if ((comment.getBlog().getId() == blogID) && (user.getId() == comment.getUsers().getId())) {
			comment.setComment(commentReq.getComment());
			commentRepository.save(comment);
			return comment;
		}
		return null;
		
	}

}
