package com.yna.itprework.user.entity;

import com.yna.itprework.user.PushType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "push_type", nullable = false)
    private PushType pushType;

    private String dndTime;

    @OneToMany(mappedBy = "user")
    private List<UserCategory> categoryPreferences;

    public boolean isDndActive(LocalTime now) {
        if (dndTime == null || dndTime.equals("-")) {
            return false;
        }

        String[] parts = dndTime.split("-");
        LocalTime start = LocalTime.parse(parts[0].trim());
        LocalTime end = LocalTime.parse(parts[1].trim());

        if (start.isAfter(end)) {
            return !now.isBefore(start) || !now.isAfter(end);
        }
        return !now.isBefore(start) && !now.isAfter(end);
    }
}
