package nl.sidnlabs.entrada.initialize;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import lombok.extern.log4j.Log4j2;
import nl.sidnlabs.entrada.engine.QueryEngine;
import nl.sidnlabs.entrada.exception.ApplicationException;
import nl.sidnlabs.entrada.util.FileUtil;
import nl.sidnlabs.entrada.util.TemplateUtil;

@Log4j2
public abstract class AbstractInitializer implements Initializer {

  @Value("${entrada.database.name}")
  protected String database;

  @Value("${entrada.database.table.dns}")
  protected String tableDns;

  @Value("${entrada.database.table.icmp}")
  protected String tableIcmp;

  @Value("${entrada.location.output}")
  protected String outputLocation;

  @Value("${entrada.icmp.enable}")
  protected boolean icmpEnabled;

  @Value("${aws.encryption}")
  protected boolean encrypt;


  private QueryEngine queryEngine;
  private String scriptPrefix;

  public AbstractInitializer(QueryEngine queryEngine, String scriptPrefix) {
    this.queryEngine = queryEngine;
    this.scriptPrefix = scriptPrefix;
  }

  @PostConstruct
  public void init() {
    log.info("Perform provisioning");

    if (!initializeStorage()) {
      throw new ApplicationException("Error while initializing storage");
    }

    if (!initializeDatabase()) {
      throw new ApplicationException("Error while initializing database/tables");
    }
  }

  @Override
  public boolean initializeDatabase() {
    // create database
    Map<String, Object> parameters = dbParameters();
    String sql = TemplateUtil.template(sqlResource("create-database.sql"), parameters);
    queryEngine.execute(sql);

    // create dns table
    parameters = dnsParameters();
    sql = TemplateUtil.template(sqlResource("create-table-dns.sql"), parameters);
    queryEngine.execute(sql);

    // create icmp table
    if (icmpEnabled) {
      // create dns table
      parameters = icmpParameters();
      sql = TemplateUtil.template(sqlResource("create-table-icmp.sql"), parameters);
      queryEngine.execute(sql);
    }

    return true;
  }

  private ClassPathResource sqlResource(String script) {
    return new ClassPathResource("/sql/" + scriptPrefix + "/" + script,
        TemplateUtil.class.getClass());
  }


  private Map<String, Object> dbParameters() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("DATABASE_NAME", database);
    parameters.put("DB_LOC", outputLocation);
    return parameters;
  }


  private Map<String, Object> dnsParameters() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("DATABASE_NAME", database);
    parameters.put("TABLE_NAME", tableDns);
    parameters.put("TABLE_LOC", FileUtil.appendPath(outputLocation, tableDns));
    parameters.put("ENCRYPTED", encrypt);
    return parameters;
  }


  private Map<String, Object> icmpParameters() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("DATABASE_NAME", database);
    parameters.put("TABLE_NAME", tableIcmp);
    parameters.put("TABLE_LOC", FileUtil.appendPath(outputLocation, tableIcmp));
    parameters.put("ENCRYPTED", encrypt);
    return parameters;
  }

}