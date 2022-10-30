package com.example.hellotalk.model.user;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private UUID id;
    private String name;
    private String selfIntroduction;
    private String occupation;
    
    private Hometown hometown;
}
