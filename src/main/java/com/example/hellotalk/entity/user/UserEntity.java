package com.example.hellotalk.entity.user;

import com.example.hellotalk.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    private String dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "creation_date")
    private String creationDate; //timestamp

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

    @ManyToMany
    @JoinTable(
            name = "users_hobbies",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hobby_and_interest_id", referencedColumnName = "id"))
    private Set<HobbyAndInterestEntity> hobbyAndInterestEntities;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "hometown_id", referencedColumnName = "id")
    private HometownEntity hometownEntity;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "userReceiverEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<FollowingRequestEntity> followedByEntity;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "userSenderEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<FollowingRequestEntity> followerOfEntity;

    public static UserEntity buildUserEntityFromModel(User user) {

        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        user.getHobbyAndInterests().forEach(h -> hobbyAndInterestEntities.add(HobbyAndInterestEntity.builder()
                .id(h.getId())
                .title(h.getTitle())
                .build()));
        
        return UserEntity.builder()
                .name(user.getName())
                .dob(user.getDob())
                .gender(user.getGender())
                .subscriptionType(user.getSubscriptionType())
                .selfIntroduction(user.getSelfIntroduction())
                .creationDate(user.getCreationDate())
                .status(user.getStatus())
                .occupation(user.getOccupation())
                .handle(user.getHandle())
                .nativeLanguage(user.getNativeLanguage())
                .targetLanguage(user.getTargetLanguage())
                .placesToVisit(user.getPlacesToVisit())
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
    }
}
