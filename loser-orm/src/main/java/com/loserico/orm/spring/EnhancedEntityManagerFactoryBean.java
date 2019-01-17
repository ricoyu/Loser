package com.loserico.orm.spring;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * 通过设置mappingDirectoryLocations帮助JPA找到XML映射文件，通常在里面写named sql query
 * 只是为了兼容传统的Spring web项目
 * 
 * @author Loser
 * @since Apr 15, 2016
 * @version
 *
 */
public class EnhancedEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean {
	
	private static final Logger logger = LoggerFactory.getLogger(EnhancedEntityManagerFactoryBean.class);

	private Resource[] mappingDirectoryLocations;

	public Resource[] getMappingDirectoryLocations() {
		return mappingDirectoryLocations;
	}

	public void setMappingDirectoryLocations(Resource... mappingDirectoryLocations) throws IOException {
		this.mappingDirectoryLocations = mappingDirectoryLocations;
		for (Resource resource : mappingDirectoryLocations) {
			File dir = resource.getFile();
			if (!dir.isDirectory()) {
				throw new IllegalArgumentException(
						"Mapping directory location [" + resource + "] does not denote a directory");
			} else {
				File[] mappingFiles = dir.listFiles();
				String[] mappingFileNames = new String[mappingFiles.length];
				for (int i = 0; i < mappingFiles.length; i++) {
					String parentFolderName = mappingFiles[i].getParentFile().getName();
					String fileName = mappingFiles[i].getName();
					mappingFileNames[i] = parentFolderName + FileSystems.getDefault().getSeparator() + fileName;
				}
				setMappingResources(mappingFileNames);
			}
		}
	}
}
