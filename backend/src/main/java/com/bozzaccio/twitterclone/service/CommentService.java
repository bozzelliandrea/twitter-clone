package com.bozzaccio.twitterclone.service;

import com.bozzaccio.twitterclone.converter.CommentConverter;
import com.bozzaccio.twitterclone.dao.CommentRepository;
import com.bozzaccio.twitterclone.dao.PostRepository;
import com.bozzaccio.twitterclone.dto.CommentDTO;
import com.bozzaccio.twitterclone.entity.Comment;
import com.bozzaccio.twitterclone.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static com.bozzaccio.twitterclone.util.ErrorUtils.*;

@Service
public class CommentService
        extends BaseCRUDServiceImpl<CommentDTO, Comment, Long>
        implements IBaseCRUDService<CommentDTO, Long> {

    private final PostRepository postRepository;

    public CommentService(CommentRepository repository,
                          CommentConverter converter,
                          PostRepository postRepository) {
        super(repository, converter);
        this.postRepository = postRepository;
    }

    @Override
    public CommentDTO update(CommentDTO dto) {
        Assert.notNull(dto.getPostId(), buildErrorMessage(BASE_PARAMETER_ERROR, ID_PARAM, NULL_MESSAGE_ERROR));
        return super.update(dto);
    }

    @Override
    public CommentDTO create(CommentDTO dto) {

        Assert.notNull(dto, buildErrorMessage(BASE_PARAMETER_ERROR, DTO, NULL_MESSAGE_ERROR));
        Assert.notNull(dto.getPostId(), buildErrorMessage(BASE_PARAMETER_ERROR, ID_PARAM, NULL_MESSAGE_ERROR));

        Optional<Post> postOptional = postRepository.findById(dto.getPostId());

        if (postOptional.isPresent()) {

            Post post = postOptional.get();

            Comment comment = converter.convertDTO(dto);
            post.increaseCommentCounter();
            comment.setPostComment(post);

            post.getComments().add(comment);

            postRepository.saveAndFlush(post);

            return dto;
        } else {
            throw new EntityNotFoundException(buildErrorMessageWithValue(BASE_DB_ERROR,
                    ENTITY_NOT_FOUND_FOR_ID_ERROR,
                    dto.getPostId()));
        }
    }
}
