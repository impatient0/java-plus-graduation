package ru.practicum.ewm.analyzer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "event_similarities", uniqueConstraints = @UniqueConstraint(columnNames = {"event_a",
    "event_b"}))
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_a", nullable = false)
    private Long eventA; // The smaller ID
    @Column(name = "event_b", nullable = false)
    private Long eventB; // The larger ID
    @Column(nullable = false)
    private Double score;
}