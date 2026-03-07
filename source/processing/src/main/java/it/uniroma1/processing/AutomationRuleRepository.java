package it.uniroma1.processing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomationRuleRepository extends JpaRepository<AutomationRule, Long> {

    AutomationRule findById(long id);

    List<AutomationRule> findByEnabledTrue();

    // Corrisponde a SELECT * FROM automation_rules WHERE sensor_name = ?
    List<AutomationRule> findBySensorName(String sensorName);

    // Corrisponde a SELECT * FROM automation_rules WHERE sensor_name = ? AND enabled = true
    List<AutomationRule> findBySensorNameAndEnabledTrue(String sensorName);

    List<AutomationRule> findByActuatorName(String actuatorName);

    // Usa @Query solo se vuoi una query più specifica e complessa
    // i.e. Tutte le rules ordinate per ID: 
    @Query("SELECT r FROM AutomationRule r WHERE r.enabled = true ORDER BY r.id ASC")
    List<AutomationRule> findAllEnabledOrdered();
}