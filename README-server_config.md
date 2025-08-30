# ✅ DNS Requirements (Before Running)
| Type  | Name  | Content       |
|-------|-------|---------------|
| CNAME | www   | trustai.co.in |
| A     | @     | 69.62.76.124  |
| A     | admin | 69.62.76.124  |



| Hostname                                      | Type | Value        |
| --------------------------------------------- | ---- | ------------ |
| trustai.co.in                                 | A    | 69.62.76.124 |
| [www.trustai.co.in](http://www.trustai.co.in) | A    | 69.62.76.124 |
| admin.trustai.co.in                           | A    | 69.62.76.124 |



# ✅ Full Script: setup_apache_ssl_trustai.sh
````shell
#!/bin/bash

# ========= Variables ==========
DOMAIN="trustai.co.in"
SUBDOMAIN="admin.${DOMAIN}"
DOC_ROOT="/var/www"
APACHE_CONF_DIR="/etc/apache2/sites-available"

echo "=============================="
echo "🚀 Starting full Apache + SSL setup"
echo "=============================="

# ========= Install Apache ==========
echo "📦 Installing Apache..."
sudo apt update
sudo apt install -y apache2

# Enable required Apache modules
sudo a2enmod rewrite ssl

# ========= Create Web Root ==========
echo "📁 Creating website directories..."
sudo mkdir -p ${DOC_ROOT}/${DOMAIN}
sudo mkdir -p ${DOC_ROOT}/${SUBDOMAIN}

echo "📝 Creating index.html files..."
echo "<h1>Welcome to ${DOMAIN}</h1>" | sudo tee ${DOC_ROOT}/${DOMAIN}/index.html
echo "<h1>Welcome to ${SUBDOMAIN}</h1>" | sudo tee ${DOC_ROOT}/${SUBDOMAIN}/index.html

# Set permissions
sudo chown -R www-data:www-data ${DOC_ROOT}
sudo chmod -R 755 ${DOC_ROOT}

# ========= Create Virtual Host Files ==========

echo "🧾 Creating Apache virtual host for ${DOMAIN}..."
sudo tee ${APACHE_CONF_DIR}/${DOMAIN}.conf > /dev/null <<EOF
<VirtualHost *:80>
    ServerName ${DOMAIN}
    ServerAlias www.${DOMAIN}
    DocumentRoot ${DOC_ROOT}/${DOMAIN}

    <Directory ${DOC_ROOT}/${DOMAIN}>
        Options Indexes FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog \${APACHE_LOG_DIR}/${DOMAIN}_error.log
    CustomLog \${APACHE_LOG_DIR}/${DOMAIN}_access.log combined
</VirtualHost>
EOF

echo "🧾 Creating Apache virtual host for ${SUBDOMAIN}..."
sudo tee ${APACHE_CONF_DIR}/${SUBDOMAIN}.conf > /dev/null <<EOF
<VirtualHost *:80>
    ServerName ${SUBDOMAIN}
    DocumentRoot ${DOC_ROOT}/${SUBDOMAIN}

    <Directory ${DOC_ROOT}/${SUBDOMAIN}>
        Options Indexes FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog \${APACHE_LOG_DIR}/${SUBDOMAIN}_error.log
    CustomLog \${APACHE_LOG_DIR}/${SUBDOMAIN}_access.log combined
</VirtualHost>
EOF

# ========= Enable Sites ==========
echo "🔗 Enabling virtual hosts..."
sudo a2dissite 000-default.conf
sudo a2ensite ${DOMAIN}.conf
sudo a2ensite ${SUBDOMAIN}.conf

# ========= Reload Apache ==========
echo "🔄 Reloading Apache..."
sudo systemctl reload apache2

# ========= Install Certbot and SSL ==========
echo "🔐 Installing Certbot and generating SSL..."
sudo apt install -y certbot python3-certbot-apache

sudo certbot --apache --non-interactive --agree-tos --redirect \
  -m admin@${DOMAIN} \
  -d ${DOMAIN} -d www.${DOMAIN} -d ${SUBDOMAIN}

# ========= Final Check ==========
echo "✅ SSL setup complete!"
echo "🌐 Visit https://${DOMAIN} and https://${SUBDOMAIN}"
echo "=============================="
````