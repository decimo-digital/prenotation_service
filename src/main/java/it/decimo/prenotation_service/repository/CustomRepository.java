package it.decimo.prenotation_service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.decimo.prenotation_service.model.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper mapper;

    /**
     * Ritorna i dati del merchant specificato
     */
    public Merchant getMerchantData(int merchantId) {
        final var sql = String.format(
                "select *,(case when total_seats = 0 then 100 else ((occupied / data.total_seats::float) * 100)::numeric(3, 2) end) as occupancy_rate from (select merchant.id, merchant.owner, merchant.store_name, merchant.location, md.openings, md.total_seats, md.description, (case when currently_prenotated is null then md.total_seats else md.total_seats - currently_prenotated end) as free_seats, (case when currently_prenotated is null then 0 else currently_prenotated end) as occupied from merchant left join (select merchant, count(prenotation.id) as currently_prenotated from prenotation right join merchant m on prenotation.merchant = m.id where prenotation.id in (select id from (select id, age(now(), to_timestamp(date_millis / 1000)) as age from prenotation) as prenotation_ages where extract(hour from prenotation_ages.age) <= 1 and extract(minute from prenotation_ages.age) <= 30) and prenotation_enabled = true group by merchant) as prenotations on id = prenotations.merchant join merchant_data md on merchant.id = md.merchant_id where merchant.id = %d) as data;",
                merchantId);

        final var map = jdbcTemplate.queryForList(sql);

        if (map.size() != 1) {
            log.error("Got an unexpected amount of merchants", map.size());
            throw new RuntimeException("Got an unexpected amount of merchants");
        }

        try {
            return mapper.readValue(mapper.writeValueAsString(map.get(0)), Merchant.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recupera l'id del proprietario del merchant richiesto
     *
     * @param merchantId id del merchant
     * @return id del proprietario, -1 se non viene trovato
     */
    public int getMerchantOwner(int merchantId) {
        final var query = "SELECT owner FROM merchant WHERE id = ?";
        final var owner = jdbcTemplate.queryForObject(query, Integer.class, merchantId);
        if (owner == null) {
            return -1;
        }
        return owner;
    }
}
