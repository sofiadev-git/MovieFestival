package it.uniroma3.siw.moviefestival_backend;

import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import it.uniroma3.siw.moviefestival_backend.repository.ProiezioneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.show-sql=false",
        "logging.level.root=ERROR",
        "logging.level.org.hibernate.SQL=OFF",
        "logging.level.org.hibernate.orm.jdbc.bind=OFF",
        "spring.main.banner-mode=off"
})
@Transactional
class AccessoDatiTest {

    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Test
    void confrontaLazyEdEntityGraph() {

        /*
         * Cerchiamo un festival che abbia almeno una proiezione.
         * Questa ricerca NON fa parte dell'esperimento.
         */
        List<Proiezione> tutteLeProiezioni =
                proiezioneRepository.findAll();

        if (tutteLeProiezioni.isEmpty()) {
            throw new IllegalStateException(
                    "Deve esistere almeno una proiezione"
            );
        }

        Long festivalId =
                tutteLeProiezioni.get(0)
                        .getFestival()
                        .getId();


        /*
         * Statistiche Hibernate.
         */
        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics =
                sessionFactory.getStatistics();

        statistics.setStatisticsEnabled(true);


        /*
         * =====================================================
         * STRATEGIA 1: LAZY
         * =====================================================
         */

        // Svuota il persistence context.
        entityManager.clear();

        // Azzera i contatori Hibernate.
        statistics.clear();


        long inizioLazy = System.nanoTime();


        List<Proiezione> proiezioniLazy =
                proiezioneRepository
                        .findAllByFestival_IdOrderByDataAscOraAsc(
                                festivalId
                        );


        /*
         * Film e Sala sono LAZY.
         * Accedendovi, Hibernate deve recuperarli.
         */
        for (Proiezione p : proiezioniLazy) {

            p.getFilm().getTitolo();

            if (p.getSala() != null) {
                p.getSala().getNome();
            }
        }


        long fineLazy = System.nanoTime();


        long queryLazy =
                statistics.getPrepareStatementCount();

        long entitaLazy =
                statistics.getEntityLoadCount();

        double tempoLazy =
                (fineLazy - inizioLazy) / 1_000_000.0;


        List<Long> idLazy =
                proiezioniLazy.stream()
                        .map(Proiezione::getId)
                        .toList();


        /*
         * =====================================================
         * STRATEGIA 2: ENTITY GRAPH
         * =====================================================
         */

        entityManager.clear();

        statistics.clear();


        long inizioEntityGraph = System.nanoTime();


        List<Proiezione> proiezioniEntityGraph =
                proiezioneRepository
                        .findByFestival_IdOrderByDataAscOraAsc(
                                festivalId
                        );


        /*
         * Eseguiamo esattamente lo stesso accesso
         * a Film e Sala.
         *
         * Con EntityGraph sono già stati caricati.
         */
        for (Proiezione p : proiezioniEntityGraph) {

            p.getFilm().getTitolo();

            if (p.getSala() != null) {
                p.getSala().getNome();
            }
        }


        long fineEntityGraph = System.nanoTime();


        long queryEntityGraph =
                statistics.getPrepareStatementCount();

        long entitaEntityGraph =
                statistics.getEntityLoadCount();

        double tempoEntityGraph =
                (fineEntityGraph - inizioEntityGraph)
                        / 1_000_000.0;


        List<Long> idEntityGraph =
                proiezioniEntityGraph.stream()
                        .map(Proiezione::getId)
                        .toList();


        /*
         * Verifica che entrambe le strategie abbiano
         * lavorato sullo stesso insieme di dati.
         */
        assertEquals(idLazy, idEntityGraph);


        /*
         * =====================================================
         * OUTPUT FINALE
         * =====================================================
         */

        System.out.println();
        System.out.println(
                "=== ANALISI ACCESSO AL PROGRAMMA DEL FESTIVAL ==="
        );

        System.out.println(
                "Festival ID: " + festivalId
        );

        System.out.println();

        System.out.println("--- Strategia 1: LAZY ---");
        System.out.println(
                "Proiezioni caricate: " + proiezioniLazy.size()
        );
        System.out.println(
                "Entità caricate: " + entitaLazy
        );
        System.out.println(
                "Query SQL eseguite: " + queryLazy
        );
        System.out.printf(
                "Tempo complessivo: %.2f ms%n",
                tempoLazy
        );


        System.out.println();

        System.out.println("--- Strategia 2: ENTITY GRAPH ---");
        System.out.println(
                "Proiezioni caricate: "
                        + proiezioniEntityGraph.size()
        );
        System.out.println(
                "Entità caricate: " + entitaEntityGraph
        );
        System.out.println(
                "Query SQL eseguite: " + queryEntityGraph
        );
        System.out.printf(
                "Tempo complessivo: %.2f ms%n",
                tempoEntityGraph
        );


        System.out.println();
        System.out.println(
                "Stesso insieme di dati: SI"
        );

        System.out.println(
                "-----------------------------------------"
        );
    }
}