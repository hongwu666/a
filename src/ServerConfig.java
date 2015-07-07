


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ServerConfig {

	private static ServerConfig instance = null;
	PropertiesConfiguration config = null;
	// ��ʼ������prop����
	private ServerConfig() throws ConfigurationException{
			config = new PropertiesConfiguration();
			config.setEncoding("UTF-8"); 
			config.load("serverConfig.properties");
	}

	/*
	 * ����ServerConfig�ĵ������
	 */
	public static ServerConfig getInstance() throws ConfigurationException {
		if(instance == null) {
			instance = new ServerConfig();
		}
		return instance;
	}


	public Configuration getConfigure() {
		return config;
	}

}

