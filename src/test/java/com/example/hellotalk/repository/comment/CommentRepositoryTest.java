package com.example.hellotalk.repository.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.repository.moment.MomentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MomentRepository momentRepository;

    @Test
    void testFindAllByMomentEntity_IdContains_ReturnsListForMoment() {
        List<CommentEntity> expectedComments = new ArrayList<>();
        CommentEntity commentEntity = CommentEntity.builder().build();
        expectedComments.add(commentEntity);

        MomentEntity momentEntity = MomentEntity.builder().build();
        momentEntity = momentRepository.save(momentEntity);
        commentEntity.setMomentEntity(momentEntity);

        commentRepository.save(commentEntity);

        List<CommentEntity> actualComments = commentRepository.findAllByMomentEntityId(momentEntity.getId());

        assertEquals(expectedComments, actualComments);
    }

    @Test
    void testFindAllByMomentEntity_IdContains_ReturnsEmptyListWhenMomentDoesNotHaveComments() {
        MomentEntity momentEntity = MomentEntity.builder().build();
        momentEntity = momentRepository.save(momentEntity);

        List<CommentEntity> actualComments = commentRepository.findAllByMomentEntityId(momentEntity.getId());

        assertEquals(0, actualComments.size());
    }
}
