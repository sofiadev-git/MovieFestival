package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Utente {

    public static final String DEFAULT_ROLE = "USER";  // accesso limitato
    public static final String ADMIN_ROLE = "ADMIN"; // accesso completo

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = false)
    private String password;

    @Column(nullable = false)
    private String role =DEFAULT_ROLE;

    @OneToMany(mappedBy = "autore")
    private Set<Recensione> recensione = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Recensione> getRecensione() {
        return recensione;
    }

    public void setRecensione(Set<Recensione> recensione) {
        this.recensione = recensione;
    }
}
