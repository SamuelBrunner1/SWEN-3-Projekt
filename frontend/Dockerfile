FROM nginx:alpine

# Eigene nginx.conf ins Image kopieren
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Statische Dateien ins Standard-Webroot
COPY . /usr/share/nginx/html
