package com.example.hellotalk.model.user;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private UUID id;
    private String name;
    private String dob;
    private String selfIntroduction;
    private String occupation;
    private String placesToVisit;
    
    private Hometown hometown;
    private Set<HobbyAndInterest> hobbyAndInterests;
}
