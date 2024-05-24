package com.zenjob.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_process")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Version
    private long version;

    @NotNull
    private UUID companyId;

    private Instant startTime;
    private Instant endTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "job", orphanRemoval = true)
    @Builder.Default
    private List<Shift> shifts = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
