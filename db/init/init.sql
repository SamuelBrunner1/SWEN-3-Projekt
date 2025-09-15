CREATE USER postgres WITH PASSWORD 'postgres';
CREATE DATABASE swenprojekt OWNER postgres;
GRANT ALL PRIVILEGES ON DATABASE swenprojekt TO postgres;
