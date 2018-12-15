3. Interface with MongoDB within the framework. Provide examples to show mapping of mongodb with Nimbus.

@Configuration
@ConfigurationProperties(prefix="process")
public class BPMEngineConfig extends AbstractProcessEngineAutoConfiguration {
	
	@Value("${process.database.driver}") 
	private String dbDriver;
	
	@Value("${process.database.url}") 
	private String dbUrl;
	
	@Value("${process.database.username}") 
	private String dbUserName;
	
	@Value("${process.database.password}") 
	private String dbPassword;
	
	@Value("${process.history.level}") 
	private String processHistoryLevel;
	
	@Value("${process.deployment.name:#{null}}")
	private Optional<String> deploymentName;

	
	
	
	
	
@Bean
  	public DataSource processDataSource() {
    		if(dbUrl.equals("embeddedH2")) {
    			return new EmbeddedDatabaseBuilder().
    					setType(EmbeddedDatabaseType.H2).
    					build();   			
    		}
    		BasicDataSource ds = new BasicDataSource();
    		ds.setUrl(dbUrl);
    		ds.setUsername(dbUserName);
    		ds.setPassword(dbPassword);
    		ds.setDriverClassName(dbDriver);
    		return ds;
  	}
	
	
	
	
	
	
application-build.yml
*******************************
process:
  key:
    regex:
     ([A-Za-z0-9_\\-\\*~]+)
  database:
    driver: 
      embeddedH2
    url: 
      embeddedH2
    username: 
      embeddedH2
    password: 
      embeddedH2
    taskUpdateQuery:
      update ACT_RU_TASK set TASK_DEF_KEY_ = ?, NAME_ = ? WHERE ID_=?
    executionUpdateQuery:
      update ACT_RU_EXECUTION set ACT_ID_ = ?  WHERE ID_=? AND ACT_ID_=?   
  history:
    level: 
      none         
	