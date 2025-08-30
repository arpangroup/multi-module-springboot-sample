## Build Docker Image
````bash
docker build -t demo-app .
````

## Run Docker Image
````bash
docker run -p 8080:8080 trustai-backend
````


## Build docker-compose:
````shell
docker compose up --build
````

### Show all Docker containers
````bash
docker ps -a
````



---

## pass custom .env file if have anything
````shell
docker compose --env-file custom.env up --build
````

## ✅ Where Are Docker Volumes Stored on Windows?
🧠 Behind the scenes:
- **Docker Desktop on Windows** typically runs containers inside a Linux VM via WSL2 (or previously via Hyper-V).
- **Docker volumes** (like your mysql_data) are stored **inside that WSL2 VM**, not directly in `C:\....`

## 🔍 To locate the volume in WSL2:
You can access Docker’s internal volume storage from WSL2 shell.
1. Open a WSL2 terminal (e.g., Ubuntu from Microsoft Store, or wsl.exe)
2. Run:
    ````bash
    docker volume inspect mysql_data
    ````
    Output:
    ````json
    [
      {
        "Name": "mysql_data",
        "Mountpoint": "/var/lib/docker/volumes/mysql_data/_data",
        ...
      }
    ]
    ````
3. You can go there using:
````bash
cd /var/lib/docker/volumes/mysql_data/_data
ls
````

## 🛠 Alternative: Use a Bind Mount (if you want data in your Windows file system)
If you want the MySQL data **stored in your Windows** folder (e.g., `C:/data/mysql`), use a **bind mount** instead of a named volume.

### 🧾 Modify docker-compose.yml:
````yaml
volumes:
  - /c/data/mysql:/var/lib/mysql
````
This maps `C:\data\mysql` on your Windows machine to the MySQL data directory.



---

## How do you change configuration (like DB credentials) without rebuilding the image?
You're using **Docker** to package your Spring Boot app. The `application.properties` or `application.yml` is **baked into the JAR inside the image**, so...

> What If You Really Need to Change application.properties Inside the Container?

> How can I update these kinds of configs in a containerized Spring Boot app without rebuilding the Docker image every time?
You have 2 less ideal options:
### 1. Externalize `application.yml` and Mount via Volume
````yml
volumes:
  - ./config/application-prod.yml:/app/config/application.yml
````
````yml
services:
  app:
    image: your-app-image
    volumes:
      - ./prod-config/application.yml:/config/application.yml
    environment:
      SPRING_CONFIG_ADDITIONAL_LOCATION: file:/config/
      SPRING_PROFILES_ACTIVE: prod
````
Spring Boot will override the internal config with the mounted one. <br/>

#### or You must configure Spring Boot to look in `/app/config`:
````bash
-Dspring.config.additional-location=file:/app/config/
````

### 🔐 Example Directory Structure (External Config)
````
project-root/
├── docker-compose.yml
├── .env
├── prod-config/
│   └── application.yml

````

### 2. Use Command-Line Overrides
````bash
docker run \
  -e SPRING_DATASOURCE_URL=... \
  your-app:latest \
  java -jar yourapp.jar --spring.config.location=...
````

---




















