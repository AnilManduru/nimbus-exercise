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
	  
	  
***************************************************************************

Spring framework provides a interface MongoOperations to interact with the Mongodatabase

import org.springframework.data.mongodb.core.MongoOperations;


Below are the code snippets of getting data from the Mongo database

@Override
	public <T> Object search(Class<T> referredClass, String alias, SearchCriteria<?> criteria) {
		Query query = buildQuery(referredClass, alias, criteria.getWhere());
		
		if(StringUtils.equalsIgnoreCase(criteria.getAggregateCriteria(),Constants.SEARCH_REQ_AGGREGATE_COUNT.code)){
			return getMongoOps().count(query, referredClass, alias);
		}
		
		if(criteria.getProjectCriteria() != null && StringUtils.isNotBlank(criteria.getProjectCriteria().getAlias())) {
			referredClass = (Class<T>)findOutputClass(criteria, referredClass);
		}
		
		if(criteria.getPageRequest() != null) {
			return findAllPageable(referredClass, alias, criteria.getPageRequest(), query);
		}
		
		return getMongoOps().find(query, referredClass, alias);
		
	}

	private <T> Query buildQuery(Class<?> referredClass, String alias, T criteria) {
		if(criteria == null) 
			return new Query();
		
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withIgnoreNullValues().withIgnorePaths("version");
		
		matcher = recurseAllFieldsAndBuildMatcher(referredClass, criteria, matcher);
		
		Example<T> example =  Example.of(criteria, matcher);
		Criteria c = Criteria.byExample(example);
		Query query = new Query(c);
		return query;
	}
	
	private <T> PageRequestAndRespone<T> findAllPageable(Class<T> referredClass, String alias, Pageable pageRequest, Query query) {
		Query qPage = query.with(pageRequest);
		
		List<T> results = getMongoOps().find(qPage, referredClass, alias);
		
		if(CollectionUtils.isEmpty(results))
			return null;
		
		return new PageRequestAndRespone<T>(results, pageRequest, () -> getMongoOps().count(query, referredClass, alias));
		
	}


