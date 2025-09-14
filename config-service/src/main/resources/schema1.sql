CREATE TABLE config_properties (
  id BIGINT NOT NULL AUTO_INCREMENT,
  application VARCHAR(50) NOT NULL,
  profile VARCHAR(10) NOT NULL,
  config_key VARCHAR(255) NOT NULL,
  config_value VARCHAR(255),
  value_type VARCHAR(20),
  enum_values VARCHAR(255),
  label VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY UK_config_key_app_profile (config_key, application, profile)
);