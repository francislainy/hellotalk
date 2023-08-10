package com.example.hellotalk.entity.user;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private static final ModelMapper modelMapper = new ModelMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    private String dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "creation_date")
    private String creationDate; // todo: timestamp - 21/05/2023

    @Column(name = "handle")
    private String handle;

    @Column(name = "status")
    private String status;

    @Column(name = "native_language")
    private String nativeLanguage;

    @Column(name = "target_language")
    private String targetLanguage;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "places_to_visit")
    private String placesToVisit;

    @Column(name = "subscription_type")
    private String subscriptionType;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "users_hobbies",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hobby_and_interest_id", referencedColumnName = "id"))
    private Set<HobbyAndInterestEntity> hobbyAndInterestEntities;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hometown_id", referencedColumnName = "id")
    private HometownEntity hometownEntity;

    @OneToMany(mappedBy = "userToEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<FollowshipEntity> followedByEntity;

    @OneToMany(mappedBy = "userFromEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<FollowshipEntity> followerOfEntity;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<MomentEntity> momentEntitySet;

    static {
        modelMapper.createTypeMap(User.class, UserEntity.class)
                .addMappings(mapper -> {
                    mapper.map(User::getHometown, UserEntity::setHometownEntity);
                    mapper.map(User::getHobbyAndInterests, UserEntity::setHobbyAndInterestEntities);
                });
    }

    public static UserEntity buildUserEntityFromModel(User user) {
        return modelMapper.map(user, UserEntity.class);
    }
}
