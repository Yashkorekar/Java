# 🚀 Cloud, Docker, Kubernetes & CI/CD Interview Guide

## 📋 Table of Contents
1. [Docker Deep Dive](#docker-deep-dive)
2. [Kubernetes Fundamentals](#kubernetes-fundamentals)
3. [CI/CD Pipeline](#cicd-pipeline)
4. [Cloud Platforms (AWS, Azure, GCP)](#cloud-platforms)
5. [Scaling Strategies](#scaling-strategies)
6. [DevOps Best Practices](#devops-best-practices)
7. [Interview Questions & Answers](#interview-questions-answers)
8. [Your URL Shortener - Cloud Deployment](#url-shortener-cloud-deployment)

---

## 🐳 Docker Deep Dive

### **Q: What is Docker and why do we use it?**

**Answer:**
> "Docker is a containerization platform that packages applications with all their dependencies into lightweight, portable containers. It solves the 'it works on my machine' problem by ensuring consistent environments across development, staging, and production."

**Key Benefits:**
1. **Consistency:** Same environment everywhere
2. **Isolation:** Each container is isolated
3. **Portability:** Run anywhere Docker is installed
4. **Efficiency:** Lightweight compared to VMs
5. **Scalability:** Easy to scale horizontally

---

### **Q: Docker vs Virtual Machine - Explain the difference**

**Visual Comparison:**

```
VIRTUAL MACHINES                    DOCKER CONTAINERS
┌─────────────────────┐            ┌─────────────────────┐
│   App A   │  App B  │            │  App A  │  App B    │
├───────────┴─────────┤            ├─────────┴───────────┤
│   Guest OS (Ubuntu) │            │  Container Runtime  │
├─────────────────────┤            ├─────────────────────┤
│     Hypervisor      │            │    Docker Engine    │
├─────────────────────┤            ├─────────────────────┤
│     Host OS         │            │      Host OS        │
└─────────────────────┘            └─────────────────────┘
     ↑                                     ↑
Size: GBs (Heavy)                  Size: MBs (Lightweight)
Boot: Minutes                      Boot: Seconds
Isolation: Strong                  Isolation: Process-level
```

| Feature | Virtual Machine | Docker Container |
|---------|----------------|------------------|
| **OS** | Full guest OS | Shares host OS kernel |
| **Size** | GBs (1-10 GB) | MBs (10-500 MB) |
| **Startup** | Minutes | Seconds |
| **Performance** | Overhead | Near-native |
| **Isolation** | Strong (hardware-level) | Process-level |
| **Use case** | Different OS needed | Same OS, different apps |

---

### **Q: Explain your Dockerfile for URL Shortener**

**Your Dockerfile (Multi-stage build):**

```dockerfile
# Stage 1: Build stage - Compile Java application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage - Run application
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENV SERVER_PORT=8081 \
    APP_BASE_URL=http://localhost:8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Line-by-line explanation:**

#### **Stage 1: Build**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
```
- **Base image:** Maven with JDK 17 (build tools included)
- **AS build:** Names this stage for reference later
- **Why JDK?** Need full JDK for compilation

```dockerfile
WORKDIR /app
```
- **Sets working directory** inside container to `/app`
- All subsequent commands run from here

```dockerfile
COPY pom.xml .
RUN mvn dependency:resolve -B
```
- **Copy pom.xml first** (before source code)
- **Download dependencies** early
- **Why?** Docker caches layers. If source changes but dependencies don't, this layer is reused (faster builds!)
- **-B:** Batch mode (non-interactive)

```dockerfile
COPY src ./src
RUN mvn clean package -DskipTests
```
- **Copy source code**
- **Build application** (compiles, tests skipped for faster builds)
- **Output:** `target/url-shortener-0.0.1-SNAPSHOT.jar`

#### **Stage 2: Runtime**
```dockerfile
FROM eclipse-temurin:17-jre
```
- **Base image:** JRE only (no JDK, smaller image!)
- **Size:** JDK ~400MB, JRE ~200MB (50% reduction)

```dockerfile
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
```
- **Copy JAR from build stage**
- **Multi-stage benefit:** Final image doesn't include Maven, source code, or build tools
- **Result:** Production image is minimal

```dockerfile
EXPOSE 8081
```
- **Documents** which port the app uses
- **Note:** Doesn't actually publish the port (that's done with `docker run -p`)

```dockerfile
ENV SERVER_PORT=8081 \
    APP_BASE_URL=http://localhost:8081
```
- **Environment variables** for Spring Boot
- **Override at runtime:** `docker run -e APP_BASE_URL=https://myapp.com`

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```
- **Command to run** when container starts
- **Exec form** (JSON array) - better than shell form
- **Why?** Proper signal handling (SIGTERM for graceful shutdown)

---

### **Q: What is multi-stage build and why use it?**

**Answer:**

**Without Multi-stage (Bad):**
```dockerfile
FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
COPY . .
RUN mvn clean package
CMD ["java", "-jar", "target/app.jar"]

Final image size: ~800 MB (includes Maven, JDK, source code, .git, etc.)
```

**With Multi-stage (Good):**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
# ... build steps

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

Final image size: ~250 MB (only JRE + JAR)
```

**Benefits:**
1. **Smaller images:** 70% size reduction
2. **Security:** No build tools in production
3. **Faster deployment:** Less data to transfer
4. **Separation:** Build and runtime environments isolated

---

### **Q: Explain Docker networking**

**Answer:**

**Network Types:**

#### **1. Bridge (Default)**
```bash
docker run --name app1 myapp
docker run --name app2 myapp

# Containers can communicate via container names
# app1 can ping app2
```

**Use case:** Multiple containers on same host

#### **2. Host**
```bash
docker run --network host myapp
# Container shares host's network
# App on port 8081 → directly accessible on host:8081
```

**Use case:** Maximum performance (no NAT overhead)

#### **3. None**
```bash
docker run --network none myapp
# No networking (isolated)
```

**Use case:** Security-critical apps

#### **4. Custom Bridge**
```bash
docker network create my-network
docker run --network my-network --name db postgres
docker run --network my-network --name app myapp

# app can access db via hostname: jdbc:postgresql://db:5432
```

**Use case:** Multi-container applications (like docker-compose)

---

### **Q: Docker Compose - Why and when to use it?**

**Answer:**

**Problem without Docker Compose:**
```bash
# Start database
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=secret \
  -p 5432:5432 \
  postgres

# Create network
docker network create myapp-network
docker network connect myapp-network postgres

# Start Redis
docker run -d --name redis \
  --network myapp-network \
  redis

# Start application
docker run -d --name app \
  --network myapp-network \
  -e DB_HOST=postgres \
  -e REDIS_HOST=redis \
  -p 8081:8081 \
  myapp

# 4 commands, easy to forget, hard to reproduce!
```

**Solution with Docker Compose:**

**Your docker-compose.yml (Enhanced):**
```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    container_name: url-shortener-db
    environment:
      POSTGRES_DB: urlshortener
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: ${DB_PASSWORD:-secret}
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: url-shortener-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
    networks:
      - app-network

  # Spring Boot Application
  url-shortener:
    build:
      context: .
      dockerfile: Dockerfile
    image: yashkorekar/url-shortener:latest
    container_name: url-shortener-app
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      # Server
      SERVER_PORT: 8081
      APP_BASE_URL: ${APP_BASE_URL:-http://localhost:8081}
      
      # Database
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/urlshortener
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-secret}
      
      # Redis
      REDIS_ENABLED: true
      REDIS_HOST: redis
      REDIS_PORT: 6379
      
      # JVM Options
      JAVA_OPTS: "-Xms256m -Xmx512m"
    ports:
      - "8081:8081"
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

networks:
  app-network:
    driver: bridge

volumes:
  postgres-data:
  redis-data:
```

**Commands:**
```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and restart
docker-compose up -d --build

# Scale app (3 instances)
docker-compose up -d --scale url-shortener=3
```

**Benefits:**
- **Single command:** `docker-compose up`
- **Declarative:** Define infrastructure as code
- **Reproducible:** Same setup everywhere
- **Versioned:** Commit docker-compose.yml to Git
- **Dependencies:** `depends_on` ensures correct startup order

---

### **Q: Docker volumes vs bind mounts**

**Answer:**

#### **1. Volumes (Recommended)**
```yaml
volumes:
  - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:  # Docker manages this
```

**Managed by Docker:**
- Location: `/var/lib/docker/volumes/`
- Backup: `docker run --volumes-from`
- Portable across hosts

#### **2. Bind Mounts**
```yaml
volumes:
  - ./logs:/app/logs  # Host directory → Container directory
```

**Direct mapping:**
- Use host filesystem
- Good for development (hot reload)
- Platform-specific paths

#### **3. tmpfs (Memory)**
```yaml
tmpfs:
  - /tmp
```

**In-memory:**
- Fast, temporary
- Lost on container stop

---

### **Q: Docker best practices**

**Answer:**

**1. Use Specific Tags (Not :latest)**
```dockerfile
# Bad
FROM openjdk:17

# Good
FROM eclipse-temurin:17.0.9_9-jre-alpine
```

**2. Minimize Layers**
```dockerfile
# Bad (3 layers)
RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y vim

# Good (1 layer)
RUN apt-get update && apt-get install -y \
    curl \
    vim \
    && rm -rf /var/lib/apt/lists/*
```

**3. Order Matters (Cache)**
```dockerfile
# Bad (cache invalidated on every code change)
COPY . .
RUN npm install

# Good (dependencies cached)
COPY package.json .
RUN npm install
COPY . .
```

**4. Use .dockerignore**
```
# .dockerignore
node_modules
target
.git
.env
*.md
```

**5. Non-root User**
```dockerfile
FROM eclipse-temurin:17-jre
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser
WORKDIR /app
COPY --chown=appuser:appuser app.jar .
CMD ["java", "-jar", "app.jar"]
```

**6. Health Checks**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8081/health || exit 1
```

---

## ☸️ Kubernetes Fundamentals

### **Q: What is Kubernetes and why use it?**

**Answer:**
> "Kubernetes (K8s) is a container orchestration platform that automates deployment, scaling, and management of containerized applications. It solves problems like container scheduling, load balancing, self-healing, and rolling updates at scale."

**Problems Kubernetes Solves:**

**Without Kubernetes:**
```
100 Docker containers across 10 servers
  → Manual deployment
  → Manual scaling
  → Manual load balancing
  → Manual restart on failure
  → No rolling updates
```

**With Kubernetes:**
```
Define desired state → Kubernetes makes it happen
  ✅ Auto-scaling
  ✅ Self-healing
  ✅ Load balancing
  ✅ Rolling updates
  ✅ Service discovery
```

---

### **Q: Kubernetes Architecture - Explain components**

**Visual Architecture:**

```
┌─────────────────────────────────────────────────────────────┐
│                    KUBERNETES CLUSTER                       │
├─────────────────────────────────────────────────────────────┤
│                     CONTROL PLANE                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ API Server   │  │  Scheduler   │  │  Controller  │      │
│  │ (kubectl →)  │  │ (assigns pods│  │  Manager     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐                                           │
│  │    etcd      │  (Distributed key-value store)            │
│  └──────────────┘                                           │
├─────────────────────────────────────────────────────────────┤
│                      WORKER NODES                           │
│  ┌────────────────────────────────────────────────┐         │
│  │  Node 1                                        │         │
│  │  ┌────────────┐  ┌────────────┐                │         │
│  │  │  kubelet   │  │ kube-proxy │                │         │
│  │  └────────────┘  └────────────┘                │         │
│  │  ┌───────────────────────────────────────┐    │         │
│  │  │        Container Runtime (Docker)      │    │         │
│  │  │  ┌─────┐  ┌─────┐  ┌─────┐            │    │         │
│  │  │  │ Pod │  │ Pod │  │ Pod │            │    │         │
│  │  │  └─────┘  └─────┘  └─────┘            │    │         │
│  │  └───────────────────────────────────────┘    │         │
│  └────────────────────────────────────────────────┘         │
│  ┌────────────────────────────────────────────────┐         │
│  │  Node 2 (similar structure)                   │         │
│  └────────────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

**Components:**

#### **Control Plane (Master Node)**

| Component | Purpose |
|-----------|---------|
| **API Server** | Entry point for all REST commands (kubectl talks to this) |
| **Scheduler** | Assigns pods to nodes based on resource availability |
| **Controller Manager** | Runs controllers (ReplicaSet, Deployment, etc.) |
| **etcd** | Distributed key-value store (cluster state) |

#### **Worker Nodes**

| Component | Purpose |
|-----------|---------|
| **kubelet** | Agent that runs on each node, manages pods |
| **kube-proxy** | Network proxy, handles service networking |
| **Container Runtime** | Docker, containerd, CRI-O |

---

### **Q: Node vs Pod - What's the Difference?**

**Short Answer:**
> "A **Node** is a physical/virtual machine (server) in the cluster. A **Pod** is the smallest deployable unit that runs on a Node and contains one or more containers."

**Analogy:**
```
Node = Apartment Building
Pod = Apartment
Container = Person living in apartment

One building (Node) can have many apartments (Pods)
Each apartment (Pod) can have one or more people (Containers)
```

---

#### **Detailed Comparison:**

| Aspect | Node | Pod |
|--------|------|-----|
| **What is it?** | Physical or virtual machine (server) | Smallest unit that runs containers |
| **Runs on** | Bare metal or VM (AWS EC2, GCP Compute) | Runs on a Node |
| **Contains** | Multiple Pods | One or more Containers |
| **Managed by** | Cluster admin (you provision nodes) | Kubernetes (you define pods in YAML) |
| **Lifespan** | Long-lived (until you delete server) | Ephemeral (can be killed and recreated) |
| **IP Address** | Node IP (e.g., 192.168.1.10) | Pod IP (e.g., 10.244.0.5) |
| **Resources** | CPU cores, RAM, disk (e.g., 8 cores, 32GB) | Resource requests/limits (e.g., 250m CPU, 256Mi RAM) |
| **Scaling** | Add more nodes to cluster | Add more pod replicas |
| **Similar to** | Your laptop, Render server, EC2 instance | Docker container(s) grouped together |

---

#### **Visual Representation:**

```
┌─────────────────────────────────────────────────────────────────┐
│                         KUBERNETES CLUSTER                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────┐          │
│  │  NODE 1 (Worker Node)                             │          │
│  │  Type: EC2 t3.large (2 vCPU, 8GB RAM)             │          │
│  │  IP: 192.168.1.10                                 │          │
│  │                                                    │          │
│  │  ┌─────────────────────────────────────────┐      │          │
│  │  │  Pod 1                                  │      │          │
│  │  │  Name: url-shortener-pod-abc123         │      │          │
│  │  │  IP: 10.244.1.5                         │      │          │
│  │  │  ┌─────────────────────────────┐        │      │          │
│  │  │  │  Container 1                │        │      │          │
│  │  │  │  Image: url-shortener:v1    │        │      │          │
│  │  │  │  Port: 8081                 │        │      │          │
│  │  │  │  Resources: 250m CPU, 256Mi │        │      │          │
│  │  │  └─────────────────────────────┘        │      │          │
│  │  └─────────────────────────────────────────┘      │          │
│  │                                                    │          │
│  │  ┌─────────────────────────────────────────┐      │          │
│  │  │  Pod 2                                  │      │          │
│  │  │  Name: redis-pod-xyz789                 │      │          │
│  │  │  IP: 10.244.1.6                         │      │          │
│  │  │  ┌─────────────────────────────┐        │      │          │
│  │  │  │  Container 1                │        │      │          │
│  │  │  │  Image: redis:7-alpine      │        │      │          │
│  │  │  │  Port: 6379                 │        │      │          │
│  │  │  └─────────────────────────────┘        │      │          │
│  │  └─────────────────────────────────────────┘      │          │
│  │                                                    │          │
│  │  Available Resources: 1.5 vCPU, 7.5GB RAM left    │          │
│  └────────────────────────────────────────────────────┘          │
│                                                                 │
│  ┌───────────────────────────────────────────────────┐          │
│  │  NODE 2 (Worker Node)                             │          │
│  │  Type: EC2 t3.large (2 vCPU, 8GB RAM)             │          │
│  │  IP: 192.168.1.11                                 │          │
│  │                                                    │          │
│  │  ┌─────────────────────────────────────────┐      │          │
│  │  │  Pod 3                                  │      │          │
│  │  │  Name: url-shortener-pod-def456         │      │          │
│  │  │  IP: 10.244.2.5                         │      │          │
│  │  │  ┌─────────────────────────────┐        │      │          │
│  │  │  │  Container 1                │        │      │          │
│  │  │  │  Image: url-shortener:v1    │        │      │          │
│  │  │  │  Port: 8081                 │        │      │          │
│  │  │  └─────────────────────────────┘        │      │          │
│  │  └─────────────────────────────────────────┘      │          │
│  │                                                    │          │
│  │  ┌─────────────────────────────────────────┐      │          │
│  │  │  Pod 4                                  │      │          │
│  │  │  Name: postgres-pod-ghi789              │      │          │
│  │  │  IP: 10.244.2.6                         │      │          │
│  │  │  ┌─────────────────────────────┐        │      │          │
│  │  │  │  Container 1                │        │      │          │
│  │  │  │  Image: postgres:15-alpine  │        │      │          │
│  │  │  │  Port: 5432                 │        │      │          │
│  │  │  └─────────────────────────────┘        │      │          │
│  │  └─────────────────────────────────────────┘      │          │
│  │                                                    │          │
│  │  Available Resources: 1.5 vCPU, 7GB RAM left      │          │
│  └────────────────────────────────────────────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

#### **Key Differences Explained:**

**1. Node = Physical/Virtual Machine (Server)**
```
Node is like:
- Your laptop
- Render server
- AWS EC2 instance
- DigitalOcean droplet
- Google Cloud Compute Engine VM

You SSH into a node:
$ ssh ubuntu@192.168.1.10
```

**2. Pod = Logical group of containers**
```
Pod is like:
- A running instance of your application
- Docker container(s) grouped together
- Temporary, can be killed and recreated

You don't SSH into pods, you exec:
$ kubectl exec -it url-shortener-pod-abc123 -- /bin/bash
```

---

#### **Real-World Example:**

**Scenario:** Running your URL Shortener on Kubernetes

**Option 1: Small Setup (1 Node)**
```
1 Node (EC2 t3.medium - 2 vCPU, 4GB RAM)
  ├─ Pod 1: url-shortener-app
  ├─ Pod 2: redis
  └─ Pod 3: postgres

Problem: If Node fails, ENTIRE system down!
```

**Option 2: Production Setup (3 Nodes)**
```
Node 1 (192.168.1.10)
  ├─ Pod 1: url-shortener-app (replica 1)
  └─ Pod 4: redis (replica 1)

Node 2 (192.168.1.11)
  ├─ Pod 2: url-shortener-app (replica 2)
  └─ Pod 5: postgres (primary)

Node 3 (192.168.1.12)
  ├─ Pod 3: url-shortener-app (replica 3)
  └─ Pod 6: postgres (replica)

Benefit: If Node 1 fails, Pods 2 and 3 still serve traffic!
```

---

#### **Node vs Pod in Commands:**

```bash
# List nodes (servers in cluster)
kubectl get nodes
# Output:
# NAME            STATUS   ROLES           AGE   VERSION
# node-1          Ready    control-plane   5d    v1.28.0
# node-2          Ready    worker          5d    v1.28.0
# node-3          Ready    worker          5d    v1.28.0

# Describe node (see resources, pods running on it)
kubectl describe node node-1

# List pods (running applications)
kubectl get pods
# Output:
# NAME                            READY   STATUS    RESTARTS   AGE
# url-shortener-pod-abc123        1/1     Running   0          2h
# url-shortener-pod-def456        1/1     Running   0          2h
# redis-pod-xyz789                1/1     Running   0          3h

# Describe pod (see which node it's running on)
kubectl describe pod url-shortener-pod-abc123
# Shows:
# Node: node-2/192.168.1.11
```

---

#### **Can a Node and a Pod be Considered the Same?**

**Answer: NO! Completely different concepts.**

**Node:**
- ✅ Hardware/VM (physical or virtual machine)
- ✅ Long-lived (exists until you delete server)
- ✅ Has OS (Linux, usually Ubuntu)
- ✅ Expensive to add (provision new server)
- ❌ Not managed by Kubernetes (you provision it)

**Pod:**
- ✅ Software/Container (runs ON a node)
- ✅ Ephemeral (temporary, can be killed anytime)
- ❌ No OS (shares node's kernel)
- ✅ Cheap to add (just scale replicas)
- ✅ Managed by Kubernetes (auto-created, auto-deleted)

**Comparison to Render:**
```
Render Server = Node (1 VM running your app)
Your Docker Container on Render = Pod (application running on that VM)
```

**When you deploy to Render:**
- Render gives you 1 Node (their server)
- Your app runs in 1 Pod on that Node
- If traffic increases, Render can:
  - Add more Pods (horizontal scaling) ← Cheap
  - Upgrade Node size (vertical scaling) ← Expensive

---

#### **Interview Question: "How do Nodes and Pods relate?"**

**Perfect Answer:**
> "A **Node** is a worker machine (server) in the Kubernetes cluster, while a **Pod** is the smallest deployable unit that runs on a Node. 
> 
> Think of it like an apartment building:
> - **Node** = The building itself (physical structure)
> - **Pod** = An apartment in the building (living space)
> - **Container** = Person living in the apartment (your application)
> 
> One Node can run many Pods, and Kubernetes decides which Node to place each Pod on based on available resources. If a Node fails, Kubernetes automatically reschedules the Pods to other healthy Nodes.
> 
> In my URL shortener project:
> - In production, I'd have **3 Nodes** (for high availability)
> - Each Node runs **multiple Pods** of my application (for load balancing)
> - If one Node goes down, the other two Nodes continue serving traffic"

---

### **Q: Kubernetes Objects - Pod, Deployment, Service, ConfigMap**

#### **1. Pod - Smallest Deployable Unit**

**What is it?**
> One or more containers that share network and storage

```yaml
# Simple Pod
apiVersion: v1
kind: Pod
metadata:
  name: url-shortener-pod
  labels:
    app: url-shortener
spec:
  containers:
  - name: url-shortener
    image: yashkorekar/url-shortener:latest
    ports:
    - containerPort: 8081
    env:
    - name: SERVER_PORT
      value: "8081"
    resources:
      requests:
        memory: "256Mi"
        cpu: "250m"
      limits:
        memory: "512Mi"
        cpu: "500m"
```

**Note:** Rarely create Pods directly. Use Deployments instead!

---

#### **2. Deployment - Manages Pods**

**What is it?**
> Manages ReplicaSets and Pods, handles rolling updates

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: url-shortener-deployment
  labels:
    app: url-shortener
spec:
  replicas: 3  # 3 pod instances
  selector:
    matchLabels:
      app: url-shortener
  template:  # Pod template
    metadata:
      labels:
        app: url-shortener
    spec:
      containers:
      - name: url-shortener
        image: yashkorekar/url-shortener:v1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SERVER_PORT
          value: "8081"
        - name: REDIS_HOST
          value: redis-service
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres-service:5432/urlshortener
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: app-secrets
        livenessProbe:
          httpGet:
            path: /health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8081
          initialDelaySeconds: 20
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # Max 1 extra pod during update
      maxUnavailable: 0  # Keep all pods running during update
```

**Commands:**
```bash
# Create deployment
kubectl apply -f deployment.yaml

# Scale
kubectl scale deployment url-shortener-deployment --replicas=5

# Update image (rolling update)
kubectl set image deployment/url-shortener-deployment \
  url-shortener=yashkorekar/url-shortener:v2.0.0

# Rollback
kubectl rollout undo deployment/url-shortener-deployment

# Check status
kubectl rollout status deployment/url-shortener-deployment

# View history
kubectl rollout history deployment/url-shortener-deployment
```

---

#### **3. Service - Networking & Load Balancing**

**What is it?**
> Exposes pods to network, provides load balancing

**Types:**

**a) ClusterIP (Default) - Internal access only**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: url-shortener-service
spec:
  selector:
    app: url-shortener
  ports:
  - protocol: TCP
    port: 80        # Service port
    targetPort: 8081  # Container port
  type: ClusterIP
```

**b) NodePort - External access via node IP**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: url-shortener-nodeport
spec:
  selector:
    app: url-shortener
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8081
    nodePort: 30080  # Access via <NodeIP>:30080
  type: NodePort
```

**c) LoadBalancer - Cloud load balancer**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: url-shortener-lb
spec:
  selector:
    app: url-shortener
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8081
  type: LoadBalancer  # AWS ELB, GCP Load Balancer, Azure LB
```

**How Service Load Balancing Works:**
```
User Request → Service (url-shortener-service)
                    ↓ (Load balanced)
        ┌───────────┼───────────┐
        ↓           ↓           ↓
      Pod 1       Pod 2       Pod 3
    (8081)      (8081)      (8081)
```

---

#### **4. ConfigMap - Configuration**

**What is it?**
> Store non-sensitive configuration data

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  SERVER_PORT: "8081"
  APP_BASE_URL: "https://short.mycompany.com"
  REDIS_ENABLED: "true"
  REDIS_PORT: "6379"
  application.properties: |
    spring.application.name=url-shortener
    server.port=8081
    cache.url.ttl=3600
```

**Usage in Pod:**
```yaml
spec:
  containers:
  - name: app
    envFrom:
    - configMapRef:
        name: app-config
    # OR mount as file
    volumeMounts:
    - name: config-volume
      mountPath: /app/config
  volumes:
  - name: config-volume
    configMap:
      name: app-config
```

---

#### **5. Secret - Sensitive Data**

**What is it?**
> Store sensitive data (passwords, tokens, keys)

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  # Base64 encoded (NOT encrypted!)
  DB_PASSWORD: c2VjcmV0MTIz  # echo -n 'secret123' | base64
  REDIS_PASSWORD: cmVkaXMxMjM=
stringData:
  # Plain text (automatically encoded)
  API_KEY: "my-secret-api-key"
```

**Usage:**
```yaml
spec:
  containers:
  - name: app
    env:
    - name: DB_PASSWORD
      valueFrom:
        secretKeyRef:
          name: app-secrets
          key: DB_PASSWORD
```

**Create from command line:**
```bash
kubectl create secret generic app-secrets \
  --from-literal=DB_PASSWORD=secret123 \
  --from-literal=API_KEY=my-key
```

---

#### **6. Ingress - HTTP(S) Routing**

**What is it?**
> Routes external HTTP(S) traffic to services

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: url-shortener-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - short.mycompany.com
    secretName: tls-secret
  rules:
  - host: short.mycompany.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: url-shortener-service
            port:
              number: 80
```

**How it works:**
```
User: https://short.mycompany.com/abc123
    ↓
Ingress Controller (Nginx)
    ↓ (Routes based on hostname/path)
Service: url-shortener-service
    ↓ (Load balances)
Pods: url-shortener-deployment (3 replicas)
```

---

### **Q: Kubernetes - Liveness vs Readiness Probes**

**Answer:**

#### **Liveness Probe**
> "Is the container alive? If not, restart it"

**Use case:** Detect deadlocks, infinite loops

```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 8081
  initialDelaySeconds: 30  # Wait 30s before first check
  periodSeconds: 10        # Check every 10s
  timeoutSeconds: 5        # Wait 5s for response
  failureThreshold: 3      # Restart after 3 failures
```

**What happens on failure:** Container is restarted

#### **Readiness Probe**
> "Is the container ready to serve traffic? If not, remove from service"

**Use case:** Slow startup, dependencies not ready

```yaml
readinessProbe:
  httpGet:
    path: /health
    port: 8081
  initialDelaySeconds: 20
  periodSeconds: 5
  failureThreshold: 3
```

**What happens on failure:** Pod removed from Service load balancer (no traffic sent)

**Example Scenario:**
```
Pod starts
  ↓
Liveness: ✅ (app running)
Readiness: ❌ (database not connected)
  ↓
Pod NOT receiving traffic (removed from Service)
  ↓
Database connection established
  ↓
Readiness: ✅
  ↓
Pod starts receiving traffic
```

---

### **Q: Kubernetes Autoscaling - HPA & VPA**

#### **1. Horizontal Pod Autoscaler (HPA)**
> "Add/remove pods based on CPU/memory/custom metrics"

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: url-shortener-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: url-shortener-deployment
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70  # Scale up if CPU > 70%
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300  # Wait 5min before scaling down
      policies:
      - type: Percent
        value: 50  # Scale down max 50% at a time
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100  # Scale up max 100% (double) at a time
        periodSeconds: 15
```

**How it works:**
```
Normal load (3 pods, CPU 50%)
    ↓
Traffic spike! (CPU 90%)
    ↓
HPA scales to 6 pods (doubled)
    ↓
CPU drops to 60%
    ↓
Traffic decreases (CPU 40%)
    ↓
Wait 5 minutes (stabilization)
    ↓
HPA scales down to 4 pods
```

#### **2. Vertical Pod Autoscaler (VPA)**
> "Adjust pod resource requests/limits"

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: url-shortener-vpa
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: url-shortener-deployment
  updatePolicy:
    updateMode: "Auto"  # Auto, Initial, Off
  resourcePolicy:
    containerPolicies:
    - containerName: url-shortener
      minAllowed:
        cpu: 100m
        memory: 128Mi
      maxAllowed:
        cpu: 2
        memory: 2Gi
```

**When to use:**
- **HPA:** Fluctuating load (traffic spikes)
- **VPA:** Wrong resource estimates (consistently using too much/little)

---

### **Q: Complete Kubernetes Deployment for URL Shortener**

**Full manifests:**

**1. Namespace**
```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: url-shortener
```

**2. ConfigMap**
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: url-shortener-config
  namespace: url-shortener
data:
  SERVER_PORT: "8081"
  REDIS_ENABLED: "true"
  REDIS_PORT: "6379"
  CACHE_URL_TTL: "3600"
```

**3. Secret**
```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: url-shortener-secrets
  namespace: url-shortener
type: Opaque
stringData:
  DB_PASSWORD: "your-secure-password"
  REDIS_PASSWORD: "redis-password"
```

**4. PostgreSQL Deployment**
```yaml
# postgres-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: url-shortener
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: urlshortener
        - name: POSTGRES_USER
          value: admin
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: url-shortener-secrets
              key: DB_PASSWORD
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: url-shortener
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
```

**5. Redis Deployment**
```yaml
# redis-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: url-shortener
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
        command: ["redis-server", "--requirepass", "$(REDIS_PASSWORD)"]
        env:
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: url-shortener-secrets
              key: REDIS_PASSWORD
---
apiVersion: v1
kind: Service
metadata:
  name: redis-service
  namespace: url-shortener
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
```

**6. Application Deployment**
```yaml
# app-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: url-shortener
  namespace: url-shortener
spec:
  replicas: 3
  selector:
    matchLabels:
      app: url-shortener
  template:
    metadata:
      labels:
        app: url-shortener
    spec:
      containers:
      - name: url-shortener
        image: yashkorekar/url-shortener:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres-service:5432/urlshortener
        - name: SPRING_DATASOURCE_USERNAME
          value: admin
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: url-shortener-secrets
              key: DB_PASSWORD
        - name: REDIS_HOST
          value: redis-service
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: url-shortener-secrets
              key: REDIS_PASSWORD
        envFrom:
        - configMapRef:
            name: url-shortener-config
        livenessProbe:
          httpGet:
            path: /health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: url-shortener-service
  namespace: url-shortener
spec:
  selector:
    app: url-shortener
  ports:
  - port: 80
    targetPort: 8081
  type: LoadBalancer
```

**Deploy:**
```bash
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f postgres-deployment.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f app-deployment.yaml

# Or all at once
kubectl apply -f k8s/
```

---

## 🔄 CI/CD Pipeline

### **Q: What is CI/CD?**

**Answer:**

**CI (Continuous Integration):**
> Automatically build and test code on every commit

**CD (Continuous Delivery/Deployment):**
> Automatically deploy code to production (after approval or automatically)

**Benefits:**
- Faster releases
- Fewer bugs reach production
- Automated testing
- Consistent deployments

---

### **Q: Explain your CI/CD pipeline for URL Shortener**

**GitHub Actions Workflow:**

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  DOCKER_IMAGE: yashkorekar/url-shortener
  JAVA_VERSION: '17'

jobs:
  # Job 1: Build and Test
  build-and-test:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn clean package -DskipTests

    - name: Run unit tests
      run: mvn test

    - name: Run integration tests
      run: mvn verify

    - name: Generate test coverage
      run: mvn jacoco:report

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml

    - name: Upload JAR artifact
      uses: actions/upload-artifact@v3
      with:
        name: url-shortener-jar
        path: target/*.jar

  # Job 2: Code Quality
  code-quality:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: SonarCloud Scan
      uses: SonarSource/sonarcloud-github-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

    - name: Run SpotBugs
      run: mvn spotbugs:check

  # Job 3: Security Scan
  security-scan:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Run Snyk vulnerability scan
      uses: snyk/actions/maven@master
      env:
        SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}

    - name: OWASP Dependency Check
      run: mvn dependency-check:check

  # Job 4: Build Docker Image
  build-docker:
    needs: [build-and-test, code-quality, security-scan]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to DockerHub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v4
      with:
        images: ${{ env.DOCKER_IMAGE }}
        tags: |
          type=ref,event=branch
          type=semver,pattern={{version}}
          type=semver,pattern={{major}}.{{minor}}
          type=sha,prefix={{branch}}-

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=registry,ref=${{ env.DOCKER_IMAGE }}:buildcache
        cache-to: type=registry,ref=${{ env.DOCKER_IMAGE }}:buildcache,mode=max

    - name: Run Trivy vulnerability scanner
      uses: aquasecurity/trivy-action@master
      with:
        image-ref: ${{ env.DOCKER_IMAGE }}:latest
        format: 'sarif'
        output: 'trivy-results.sarif'

    - name: Upload Trivy results to GitHub Security
      uses: github/codeql-action/upload-sarif@v2
      with:
        sarif_file: 'trivy-results.sarif'

  # Job 5: Deploy to Kubernetes
  deploy-k8s:
    needs: build-docker
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Configure kubectl
      uses: azure/k8s-set-context@v3
      with:
        kubeconfig: ${{ secrets.KUBE_CONFIG }}

    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/url-shortener \
          url-shortener=${{ env.DOCKER_IMAGE }}:${{ github.sha }} \
          -n url-shortener
        kubectl rollout status deployment/url-shortener -n url-shortener

    - name: Verify deployment
      run: |
        kubectl get pods -n url-shortener
        kubectl get services -n url-shortener

  # Job 6: Smoke Tests
  smoke-tests:
    needs: deploy-k8s
    runs-on: ubuntu-latest
    steps:
    - name: Health check
      run: |
        curl -f https://short.mycompany.com/health || exit 1

    - name: Create short URL test
      run: |
        RESPONSE=$(curl -s -X POST https://short.mycompany.com/api/shorten \
          -H "Content-Type: application/json" \
          -d '{"url":"https://google.com"}')
        echo $RESPONSE | jq -e '.shortCode'

  # Job 7: Notify
  notify:
    needs: [smoke-tests]
    runs-on: ubuntu-latest
    if: always()
    steps:
    - name: Slack notification
      uses: 8398a7/action-slack@v3
      with:
        status: ${{ job.status }}
        text: 'Deployment to production completed!'
        webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---

### **Q: CI/CD Best Practices**

**Answer:**

**1. Automated Testing Pyramid**
```
    /\
   /  \  End-to-End Tests (Few, slow, brittle)
  /    \
 /------\ Integration Tests (Some, medium speed)
/--------\
|        | Unit Tests (Many, fast, isolated)
└────────┘
```

**2. Branch Strategy**
```
main (production)
  ↑
develop (staging)
  ↑
feature/short-url-validation (PR)
feature/redis-caching (PR)
```

**3. Environment Parity**
```
Dev → Staging → Production
All use same:
  - Docker images
  - Config structure
  - Infrastructure as Code
```

**4. Rollback Strategy**
```yaml
# Kubernetes makes this easy
kubectl rollout undo deployment/url-shortener

# Or version-specific
kubectl rollout undo deployment/url-shortener --to-revision=2
```

**5. Blue-Green Deployment**
```
Production Traffic
        ↓
    Blue (v1.0)  ← Current
        
Deploy Green (v2.0)
Test Green
Switch traffic: Blue → Green
Keep Blue as rollback option
```

**6. Canary Deployment**
```
100% traffic → v1.0
↓ Deploy v2.0
90% → v1.0, 10% → v2.0 (canary)
↓ Monitor metrics
50% → v1.0, 50% → v2.0
↓ No errors
100% → v2.0
```

---

## ☁️ Cloud Platforms (AWS, Azure, GCP)

### **Q: Compare AWS, Azure, GCP for hosting URL Shortener**

| Service | AWS | Azure | GCP |
|---------|-----|-------|-----|
| **Compute** | EC2, ECS, EKS | Virtual Machines, AKS | Compute Engine, GKE |
| **Container** | ECS (Fargate), EKS | AKS, Container Instances | GKE, Cloud Run |
| **Database** | RDS (PostgreSQL) | Azure Database | Cloud SQL |
| **Cache** | ElastiCache (Redis) | Azure Cache for Redis | Memorystore |
| **Load Balancer** | ELB, ALB | Azure Load Balancer | Cloud Load Balancing |
| **Storage** | S3 | Blob Storage | Cloud Storage |
| **CDN** | CloudFront | Azure CDN | Cloud CDN |
| **DNS** | Route 53 | Azure DNS | Cloud DNS |
| **Monitoring** | CloudWatch | Azure Monitor | Cloud Monitoring |
| **CI/CD** | CodePipeline | Azure DevOps | Cloud Build |

---

### **Q: Deploy URL Shortener on AWS**

**Architecture:**

```
Internet
    ↓
Route 53 (DNS: short.company.com)
    ↓
CloudFront (CDN)
    ↓
Application Load Balancer
    ↓
┌───────────┬───────────┬───────────┐
│   EKS     │   EKS     │   EKS     │
│  Pod 1    │  Pod 2    │  Pod 3    │
│ (AZ-1a)   │ (AZ-1b)   │ (AZ-1c)   │
└─────┬─────┴─────┬─────┴─────┬─────┘
      │           │           │
      └───────────┼───────────┘
                  ↓
          ┌───────────────┐
          │ ElastiCache   │
          │    (Redis)    │
          └───────┬───────┘
                  │
          ┌───────▼───────┐
          │   RDS Aurora  │
          │  (PostgreSQL) │
          │ Primary+Read  │
          │   Replicas    │
          └───────────────┘
```

**Terraform Example:**

```hcl
# main.tf
provider "aws" {
  region = "us-east-1"
}

# VPC
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Name = "url-shortener-vpc"
  }
}

# EKS Cluster
resource "aws_eks_cluster" "main" {
  name     = "url-shortener-cluster"
  role_arn = aws_iam_role.eks_cluster.arn
  version  = "1.28"

  vpc_config {
    subnet_ids = aws_subnet.private[*].id
  }

  depends_on = [
    aws_iam_role_policy_attachment.eks_cluster_policy,
  ]
}

# RDS PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier           = "url-shortener-db"
  engine              = "postgres"
  engine_version      = "15.4"
  instance_class      = "db.t3.medium"
  allocated_storage   = 100
  storage_encrypted   = true
  
  db_name  = "urlshortener"
  username = "admin"
  password = var.db_password
  
  multi_az = true
  backup_retention_period = 7
  
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  
  tags = {
    Name = "url-shortener-db"
  }
}

# ElastiCache Redis
resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "url-shortener-redis"
  engine               = "redis"
  engine_version       = "7.0"
  node_type           = "cache.t3.medium"
  num_cache_nodes     = 1
  parameter_group_name = "default.redis7"
  port                = 6379
  
  subnet_group_name = aws_elasticache_subnet_group.main.name
  security_group_ids = [aws_security_group.redis.id]
  
  tags = {
    Name = "url-shortener-redis"
  }
}

# Application Load Balancer
resource "aws_lb" "main" {
  name               = "url-shortener-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets           = aws_subnet.public[*].id

  enable_deletion_protection = false

  tags = {
    Name = "url-shortener-alb"
  }
}
```

**Estimated Monthly Cost (AWS):**
```
EKS Cluster: $73/month
3x t3.medium nodes: $95/month
RDS db.t3.medium: $100/month
ElastiCache t3.medium: $50/month
Load Balancer: $20/month
Data Transfer: $50/month

Total: ~$388/month
```

---

### **Q: Serverless deployment options**

**AWS Lambda + API Gateway:**

```yaml
# serverless.yml
service: url-shortener

provider:
  name: aws
  runtime: java17
  region: us-east-1
  environment:
    DB_HOST: ${env:DB_HOST}
    REDIS_HOST: ${env:REDIS_HOST}

functions:
  shorten:
    handler: com.yk.urlshortener.lambda.ShortenHandler
    events:
      - http:
          path: /api/shorten
          method: post
  
  redirect:
    handler: com.yk.urlshortener.lambda.RedirectHandler
    events:
      - http:
          path: /{shortCode}
          method: get

resources:
  Resources:
    UrlsTable:
      Type: AWS::DynamoDB::Table
      Properties:
        TableName: urls
        AttributeDefinitions:
          - AttributeName: shortCode
            AttributeType: S
        KeySchema:
          - AttributeName: shortCode
            KeyType: HASH
        BillingMode: PAY_PER_REQUEST
```

**Pros:**
- No server management
- Auto-scaling
- Pay per request
- Low cost at low traffic

**Cons:**
- Cold start latency
- 15min execution limit
- Stateless only

---

## 📈 Scaling Strategies

### **Q: How to scale URL Shortener to handle 1 billion URLs?**

**Answer:**

### **Phase 1: Single Server (1K-10K requests/day)**
```
Current Architecture ✅
Spring Boot → H2 Database
```

### **Phase 2: Add Caching (10K-100K req/day)**
```
Spring Boot → Redis → MySQL
Performance: 10x improvement
```

### **Phase 3: Horizontal Scaling (100K-1M req/day)**
```
Load Balancer
    ↓
┌────────┬────────┬────────┐
App 1   App 2   App 3
    ↓       ↓       ↓
    Redis Cluster
          ↓
    MySQL (Primary + Read Replicas)
```

**Kubernetes deployment:**
```yaml
# Horizontal Pod Autoscaler
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: url-shortener-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: url-shortener
  minReplicas: 3
  maxReplicas: 50
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### **Phase 4: Database Sharding (1M-10M req/day)**

**Shard by shortCode prefix:**
```
shortCode: abc123
    ↓
Hash: hash(abc123) % 4 = 2
    ↓
Route to Shard 2

Shard 0: a-f*
Shard 1: g-m*
Shard 2: n-s*
Shard 3: t-z*
```

**Implementation:**
```java
@Service
public class ShardingService {
    
    private final Map<Integer, DataSource> shards = new HashMap<>();
    
    public DataSource getShard(String shortCode) {
        int shardId = Math.abs(shortCode.hashCode()) % 4;
        return shards.get(shardId);
    }
    
    public Url findByShortCode(String shortCode) {
        DataSource shard = getShard(shortCode);
        JdbcTemplate jdbc = new JdbcTemplate(shard);
        return jdbc.queryForObject(
            "SELECT * FROM urls WHERE short_code = ?",
            new Object[]{shortCode},
            new UrlRowMapper()
        );
    }
}
```

### **Phase 5: Microservices (10M-100M req/day)**

**Split into services:**
```
┌─────────────────────────────────────────┐
│          API Gateway (Kong/Nginx)       │
└─────────────────┬───────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    ↓             ↓             ↓
┌─────────┐  ┌─────────┐  ┌──────────┐
│Shorten  │  │Redirect │  │Analytics │
│Service  │  │Service  │  │Service   │
└────┬────┘  └────┬────┘  └────┬─────┘
     │            │            │
     ↓            ↓            ↓
┌─────────┐  ┌─────────┐  ┌──────────┐
│MySQL    │  │Redis    │  │Cassandra │
│Cluster  │  │Cluster  │  │(Analytics)│
└─────────┘  └─────────┘  └──────────┘
```

**Benefits:**
- Independent scaling
- Technology choice per service
- Fault isolation
- Faster deployments

### **Phase 6: Global Distribution (100M-1B req/day)**

**Multi-region architecture:**
```
         ┌─────────────────────────────┐
         │   Global CDN (CloudFlare)   │
         └──────────┬──────────────────┘
                    │
      ┌─────────────┼─────────────┐
      ↓             ↓             ↓
┌──────────┐  ┌──────────┐  ┌──────────┐
│US-East   │  │EU-West   │  │AP-South  │
│Region    │  │Region    │  │Region    │
├──────────┤  ├──────────┤  ├──────────┤
│Load Bal  │  │Load Bal  │  │Load Bal  │
│50 Pods   │  │50 Pods   │  │50 Pods   │
│Redis     │  │Redis     │  │Redis     │
│Cluster   │  │Cluster   │  │Cluster   │
│PostgreSQL│  │PostgreSQL│  │PostgreSQL│
│Read Rep  │  │Read Rep  │  │Read Rep  │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │            │            │
     └────────────┼────────────┘
                  ↓
         Global PostgreSQL
         (Primary - US-East)
```

**Traffic distribution:**
- User in US → US-East region
- User in Europe → EU-West region
- User in Asia → AP-South region

**Database replication:**
- Writes → Primary (US-East)
- Reads → Local read replicas
- Eventual consistency (< 100ms lag)

### **Phase 7: Extreme Scale (1B+ req/day)**

**Netflix/Twitter scale optimizations:**

**1. Consistent Hashing for Redis:**
```java
public class ConsistentHashRedis {
    private final TreeMap<Long, RedisClient> ring = new TreeMap<>();
    
    public RedisClient getClient(String key) {
        long hash = md5(key);
        Map.Entry<Long, RedisClient> entry = ring.ceilingEntry(hash);
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
    }
}
```

**2. Write-Behind Cache:**
```java
@Service
public class WriteBehindCache {
    private final BlockingQueue<Url> writeQueue = new LinkedBlockingQueue<>(10000);
    
    @PostConstruct
    public void startBatchWriter() {
        executorService.submit(() -> {
            while (true) {
                List<Url> batch = new ArrayList<>();
                writeQueue.drainTo(batch, 1000);
                
                if (!batch.isEmpty()) {
                    batchInsertToDatabase(batch);
                }
                
                Thread.sleep(1000);
            }
        });
    }
}
```

**3. Bloom Filter (Reduce DB lookups):**
```java
@Service
public class BloomFilterService {
    private final BloomFilter<String> bloomFilter = 
        BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 
                          1_000_000_000, 0.01);
    
    public boolean mightExist(String shortCode) {
        return bloomFilter.mightContain(shortCode);
    }
}
```

---

## 🛠️ DevOps Best Practices

### **Q: Infrastructure as Code (IaC)**

**Terraform vs CloudFormation vs Pulumi:**

| Feature | Terraform | CloudFormation | Pulumi |
|---------|-----------|----------------|--------|
| **Language** | HCL | JSON/YAML | Python/TS/Go |
| **Provider** | Multi-cloud | AWS only | Multi-cloud |
| **State** | Remote state | AWS managed | Remote state |
| **Learning curve** | Medium | Medium | Low (use familiar language) |

**Example: Terraform for URL Shortener:**
```hcl
# variables.tf
variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "replicas" {
  description = "Number of app replicas"
  type        = number
  default     = 3
}

# main.tf
resource "kubernetes_deployment" "url_shortener" {
  metadata {
    name      = "url-shortener"
    namespace = var.environment
  }

  spec {
    replicas = var.replicas

    selector {
      match_labels = {
        app = "url-shortener"
      }
    }

    template {
      metadata {
        labels = {
          app = "url-shortener"
        }
      }

      spec {
        container {
          image = "yashkorekar/url-shortener:${var.app_version}"
          name  = "url-shortener"

          port {
            container_port = 8081
          }

          resources {
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "256Mi"
            }
          }
        }
      }
    }
  }
}
```

---

### **Q: Monitoring and Observability**

**Three Pillars:**

#### **1. Metrics (Prometheus + Grafana)**

**Expose metrics:**
```java
@RestController
public class MetricsController {
    private final MeterRegistry meterRegistry;
    
    @PostConstruct
    public void setupMetrics() {
        meterRegistry.counter("url.shortener.requests.total").increment();
        meterRegistry.gauge("url.shortener.cache.size", cacheSize);
        meterRegistry.timer("url.shortener.response.time");
    }
}
```

**Prometheus scrape config:**
```yaml
scrape_configs:
  - job_name: 'url-shortener'
    kubernetes_sd_configs:
    - role: pod
    relabel_configs:
    - source_labels: [__meta_kubernetes_pod_label_app]
      regex: url-shortener
      action: keep
```

**Key metrics to monitor:**
- Request rate (requests/sec)
- Error rate (%)
- Response time (p50, p95, p99)
- Cache hit ratio
- Database connection pool
- JVM memory/GC

#### **2. Logs (ELK/EFK Stack)**

**Structured logging:**
```java
@Slf4j
@Service
public class UrlShortenerService {
    public Url shortenUrl(String longUrl) {
        log.info("Shortening URL: url={}, userId={}", 
                 longUrl, SecurityContextHolder.getUserId());
        
        try {
            Url url = createUrl(longUrl);
            log.info("URL shortened successfully: shortCode={}, longUrl={}", 
                     url.getShortCode(), url.getLongUrl());
            return url;
        } catch (Exception e) {
            log.error("Failed to shorten URL: url={}, error={}", 
                      longUrl, e.getMessage(), e);
            throw e;
        }
    }
}
```

**Logback configuration:**
```xml
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>true</includeContext>
            <includeMdc>true</includeMdc>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

#### **3. Tracing (Jaeger/Zipkin)**

**Distributed tracing:**
```java
@Configuration
public class TracingConfig {
    @Bean
    public Tracer jaegerTracer() {
        return Configuration.fromEnv("url-shortener")
            .getTracer();
    }
}

@Service
public class UrlShortenerService {
    @Autowired
    private Tracer tracer;
    
    public Url shortenUrl(String longUrl) {
        Span span = tracer.buildSpan("shorten-url").start();
        try {
            span.setTag("url.long", longUrl);
            
            Url url = createUrl(longUrl);
            
            span.setTag("url.short", url.getShortCode());
            return url;
        } finally {
            span.finish();
        }
    }
}
```

---

### **Q: Disaster Recovery & Backup**

**Strategy:**

**1. Database Backups:**
```bash
# Automated daily backups
0 2 * * * pg_dump -h postgres-service -U admin urlshortener > backup-$(date +\%Y\%m\%d).sql

# Retention: 7 daily, 4 weekly, 12 monthly
```

**2. Multi-AZ Deployment:**
```yaml
# Kubernetes PodDisruptionBudget
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: url-shortener-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: url-shortener
```

**3. Disaster Recovery Plan:**
```
RTO (Recovery Time Objective): 1 hour
RPO (Recovery Point Objective): 15 minutes

Scenarios:
1. Single pod failure → K8s auto-restarts (< 1min)
2. Node failure → Pods rescheduled (< 5min)
3. AZ failure → Traffic to other AZs (< 30sec)
4. Region failure → Failover to DR region (< 1hr)
```

---

## 🎤 Interview Questions & Answers

### **Docker Questions**

#### **Q1: How do you optimize Docker image size?**

**Answer:**
> "I use several techniques:
> 1. **Multi-stage builds** - Separate build and runtime images (70% size reduction)
> 2. **Alpine base images** - Smaller than Ubuntu/Debian
> 3. **Layer caching** - Order Dockerfile commands strategically
> 4. **.dockerignore** - Exclude unnecessary files
> 5. **Combine RUN commands** - Fewer layers
> 6. **Remove cache** - `rm -rf /var/lib/apt/lists/*` after apt-get"

**Example:**
```dockerfile
# Bad: 800 MB
FROM maven:3.9-jdk-17
COPY . .
RUN mvn package

# Good: 250 MB
FROM maven:3.9-jdk-17 AS build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

---

#### **Q2: Difference between CMD and ENTRYPOINT?**

**Answer:**

| Feature | CMD | ENTRYPOINT |
|---------|-----|------------|
| **Purpose** | Default command | Main command |
| **Override** | `docker run image command` | `docker run --entrypoint` |
| **Use case** | Provide defaults | Fixed command |

**Examples:**
```dockerfile
# CMD (can be overridden easily)
CMD ["java", "-jar", "app.jar"]
# docker run myapp ls  → runs ls (overrides CMD)

# ENTRYPOINT (fixed command)
ENTRYPOINT ["java", "-jar", "app.jar"]
# docker run myapp --debug  → java -jar app.jar --debug

# Combined (best practice)
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]
# docker run myapp --spring.profiles.active=dev  → overrides CMD
```

---

#### **Q3: How do you handle secrets in Docker?**

**Answer:**
> "Never store secrets in Dockerfile or image! Use:
> 
> **Option 1: Environment variables**
> ```bash
> docker run -e DB_PASSWORD=$DB_PASSWORD myapp
> ```
> 
> **Option 2: Docker secrets (Swarm)**
> ```bash
> echo "secret123" | docker secret create db_password -
> docker service create --secret db_password myapp
> ```
> 
> **Option 3: External secret management**
> - AWS Secrets Manager
> - HashiCorp Vault
> - Kubernetes Secrets
> 
> **In production, I use Kubernetes Secrets with encryption at rest.**"

---

### **Kubernetes Questions**

#### **Q4: Explain StatefulSet vs Deployment**

**Answer:**

| Feature | Deployment | StatefulSet |
|---------|-----------|-------------|
| **Pod names** | Random (pod-7f9d8-xyz) | Ordered (pod-0, pod-1, pod-2) |
| **Pod identity** | No stable identity | Stable network identity |
| **Storage** | Shared volumes | Dedicated PersistentVolume per pod |
| **Scaling** | Parallel | Ordered (0→1→2) |
| **Use case** | Stateless apps | Databases, message queues |

**When to use:**
- **Deployment:** URL Shortener app (stateless)
- **StatefulSet:** PostgreSQL, Redis cluster (stateful)

---

#### **Q5: What is the purpose of Init Containers?**

**Answer:**
> "Init containers run before app containers and must complete successfully. Use cases:
> 
> **1. Wait for dependencies:**
> ```yaml
> initContainers:
> - name: wait-for-db
>   image: busybox
>   command: ['sh', '-c', 'until nc -z postgres-service 5432; do sleep 1; done']
> ```
> 
> **2. Setup configuration:**
> ```yaml
> initContainers:
> - name: setup-config
>   image: busybox
>   command: ['sh', '-c', 'cp /config/* /app/config/']
> ```
> 
> **3. Database migrations:**
> ```yaml
> initContainers:
> - name: db-migration
>   image: flyway/flyway
>   command: ['flyway', 'migrate']
> ```"

---

#### **Q6: How do you secure Kubernetes cluster?**

**Answer:**
> "Multiple layers:
> 
> **1. RBAC (Role-Based Access Control)**
> ```yaml
> apiVersion: rbac.authorization.k8s.io/v1
> kind: Role
> metadata:
>   name: pod-reader
> rules:
> - apiGroups: [""]
>   resources: ["pods"]
>   verbs: ["get", "list"]
> ```
> 
> **2. Network Policies**
> ```yaml
> apiVersion: networking.k8s.io/v1
> kind: NetworkPolicy
> metadata:
>   name: deny-all
> spec:
>   podSelector: {}
>   policyTypes:
>   - Ingress
>   - Egress
> ```
> 
> **3. Pod Security Standards**
> - No root user
> - Read-only filesystem
> - Drop capabilities
> 
> **4. Secrets encryption at rest**
> 
> **5. Image scanning (Trivy, Snyk)**
> 
> **6. Admission controllers (OPA, Kyverno)**"

---

### **CI/CD Questions**

#### **Q7: What is GitOps?**

**Answer:**
> "GitOps is a way of implementing CD where Git is the single source of truth for infrastructure and applications.
> 
> **Traditional CD:**
> ```
> Developer → CI Pipeline → kubectl apply → Cluster
> ```
> 
> **GitOps (with ArgoCD):**
> ```
> Developer → Git commit
>     ↓
> ArgoCD watches Git repo
>     ↓
> ArgoCD syncs cluster to match Git
> ```
> 
> **Benefits:**
> - Declarative (describe desired state)
> - Versioned (Git history)
> - Auditable (who changed what)
> - Rollback (git revert)
> 
> **Tools:** ArgoCD, Flux, Jenkins X"

---

#### **Q8: How do you implement zero-downtime deployments?**

**Answer:**
> "Using Kubernetes rolling updates:
> 
> **1. Strategy configuration:**
> ```yaml
> strategy:
>   type: RollingUpdate
>   rollingUpdate:
>     maxSurge: 1        # Max 1 extra pod
>     maxUnavailable: 0  # Never reduce below desired
> ```
> 
> **2. Readiness probes:**
> ```yaml
> readinessProbe:
>   httpGet:
>     path: /health
>     port: 8081
>   initialDelaySeconds: 20
>   periodSeconds: 5
> ```
> 
> **How it works:**
> ```
> v1: [Pod-1] [Pod-2] [Pod-3]  ← 3 pods running
>      ↓
> v1: [Pod-1] [Pod-2] [Pod-3] + v2: [Pod-4]  ← Deploy new pod
>      ↓
> v1: [Pod-1] [Pod-2] [Pod-3] + v2: [Pod-4]  ← Wait for readiness
>      ↓
> v1: [Pod-1] [Pod-2] + v2: [Pod-4]  ← Terminate old pod
>      ↓
> v2: [Pod-4] [Pod-5] [Pod-6]  ← All new version
> ```
> 
> **Result:** Users always see working version, no downtime!"

---

### **Cloud Questions**

#### **Q9: How do you optimize cloud costs?**

**Answer:**
> "Multiple strategies:
> 
> **1. Right-sizing:**
> - Use smallest instance that meets requirements
> - Monitor actual usage (CPU, memory)
> - Resize based on data
> 
> **2. Autoscaling:**
> - Scale down during off-peak hours
> - Use Spot/Preemptible instances for batch jobs
> 
> **3. Reserved Instances/Savings Plans:**
> - 1-3 year commitments (40-60% discount)
> - For predictable workloads
> 
> **4. Database optimization:**
> - Use read replicas instead of bigger primary
> - Enable query caching
> - Archive old data
> 
> **5. Storage optimization:**
> - Lifecycle policies (move to cheaper tiers)
> - Compress data
> - Delete unused volumes
> 
> **6. Monitoring:**
> - AWS Cost Explorer
> - Grafana dashboards
> - Alerts on anomalies
> 
> **For URL Shortener:**
> - Use HPA (scale to 0 at night)
> - Cache aggressively (reduce DB load)
> - S3 for logs (cheaper than EBS)"

---

#### **Q10: Explain the shared responsibility model**

**Answer:**

```
┌─────────────────────────────────────────┐
│         Customer Responsibility         │
├─────────────────────────────────────────┤
│  - Application code                     │
│  - Data encryption                      │
│  - Access management (IAM)              │
│  - Network configuration                │
│  - OS patching (if EC2)                 │
│  - Security groups                      │
├─────────────────────────────────────────┤
│          AWS Responsibility             │
├─────────────────────────────────────────┤
│  - Physical security                    │
│  - Network infrastructure               │
│  - Hypervisor                          │
│  - Hardware maintenance                 │
│  - Managed service security (RDS, etc.) │
└─────────────────────────────────────────┘
```

**Example for URL Shortener:**
- **AWS:** Protects RDS database hardware
- **You:** Encrypt data, manage database users, apply security patches

---

### **Scaling Questions**

#### **Q11: What is Circuit Breaker pattern?**

**Answer:**
> "Circuit breaker prevents cascading failures when a service is down.
> 
> **States:**
> ```
> CLOSED (Normal)
>   → Failures exceed threshold
> OPEN (Failing)
>   → Wait timeout
> HALF-OPEN (Testing)
>   → Success → CLOSED
>   → Failure → OPEN
> ```
> 
> **Implementation (Resilience4j):**
> ```java
> @CircuitBreaker(name = "redis", fallbackMethod = "fallbackCache")
> public Url getCachedUrl(String shortCode) {
>     return redisTemplate.opsForValue().get(shortCode);
> }
> 
> public Url fallbackCache(String shortCode, Exception e) {
>     log.warn("Redis unavailable, using database");
>     return urlRepository.findByShortCode(shortCode)
>                        .orElseThrow(NotFoundException::new);
> }
> ```
> 
> **Benefits:**
> - Fail fast (don't wait for timeout)
> - Give service time to recover
> - Fallback to alternative"

---

#### **Q12: How to handle database connection pooling?**

**Answer:**
> "Connection pooling reuses database connections instead of creating new ones.
> 
> **HikariCP Configuration:**
> ```properties
> spring.datasource.hikari.maximum-pool-size=20
> spring.datasource.hikari.minimum-idle=5
> spring.datasource.hikari.connection-timeout=30000
> spring.datasource.hikari.idle-timeout=600000
> spring.datasource.hikari.max-lifetime=1800000
> ```
> 
> **Sizing formula:**
> ```
> connections = ((core_count * 2) + effective_spindle_count)
> 
> For 4-core CPU with SSD:
> connections = (4 * 2) + 1 = 9
> 
> Add buffer: 10-15 connections
> ```
> 
> **Monitoring:**
> ```java
> @Bean
> public MeterBinder hikariMetrics(DataSource dataSource) {
>     return new HikariDataSourcePoolMetrics(
>         (HikariDataSource) dataSource, 
>         "hikari", 
>         Collections.emptyList()
>     );
> }
> ```
> 
> **Key metrics:**
> - Active connections
> - Idle connections
> - Wait time
> - Connection creation rate"

---

## 🎯 Your URL Shortener - Complete Cloud Deployment

### **Production-Ready Deployment Checklist:**

```
✅ Docker
  ✅ Multi-stage Dockerfile
  ✅ .dockerignore
  ✅ Non-root user
  ✅ Health checks
  ✅ Pushed to Docker Hub

✅ Kubernetes
  ✅ Namespace
  ✅ Deployments (app, db, redis)
  ✅ Services (ClusterIP, LoadBalancer)
  ✅ ConfigMaps & Secrets
  ✅ Ingress (HTTPS with cert-manager)
  ✅ HPA (autoscaling)
  ✅ PodDisruptionBudget
  ✅ Resource limits/requests
  ✅ Liveness/Readiness probes

✅ CI/CD
  ✅ GitHub Actions workflow
  ✅ Automated tests
  ✅ Docker build & push
  ✅ Kubernetes deployment
  ✅ Smoke tests
  ✅ Rollback capability

✅ Monitoring
  ✅ Prometheus metrics
  ✅ Grafana dashboards
  ✅ Structured logging
  ✅ Distributed tracing
  ✅ Alerting (PagerDuty/Slack)

✅ Security
  ✅ RBAC configured
  ✅ Network policies
  ✅ Secrets encrypted
  ✅ Image scanning
  ✅ HTTPS/TLS
  ✅ Rate limiting

✅ Backup & DR
  ✅ Database backups (daily)
  ✅ Multi-AZ deployment
  ✅ Disaster recovery plan
  ✅ Tested restore procedure
```

---

## 📝 What You're Still Missing (Gaps Analysis)

### **Additional Topics for InfraCloud Interview:**

#### **1. Service Mesh (Istio/Linkerd)**
> "Service mesh handles service-to-service communication, observability, and security. While not in your current implementation, knowing the concept is valuable."

**Key features:**
- Traffic management (canary, blue-green)
- Service discovery
- Load balancing
- mTLS between services
- Circuit breaking
- Observability

#### **2. Message Queues (Kafka/RabbitMQ)**
> "For async operations like analytics, notifications. Could enhance your URL shortener."

**Use case:**
```java
// Publish URL creation event
@Service
public class UrlShortenerService {
    @Autowired
    private KafkaTemplate<String, UrlCreatedEvent> kafka;
    
    public Url shortenUrl(String longUrl) {
        Url url = createUrl(longUrl);
        
        // Publish event for analytics
        kafka.send("url-created", new UrlCreatedEvent(
            url.getShortCode(), 
            url.getLongUrl(), 
            LocalDateTime.now()
        ));
        
        return url;
    }
}
```

#### **3. API Gateway (Kong/Apigee)**
> "Centralized entry point for microservices. Handles authentication, rate limiting, caching."

**Benefits:**
- Single entry point
- Rate limiting
- Authentication/Authorization
- Request/response transformation
- Monitoring

#### **4. Chaos Engineering**
> "Test system resilience by intentionally causing failures."

**Tools:** Chaos Monkey, Litmus Chaos

**Example test:**
```bash
# Kill random pod
kubectl delete pod -l app=url-shortener --random

# Verify: System should auto-heal (new pod created)
kubectl get pods -w
```

#### **5. FinOps (Cloud Cost Management)**
> "Monitor and optimize cloud spending."

**Key practices:**
- Tagging resources
- Cost allocation
- Showback/Chargeback
- Budget alerts
- Reserved Instance recommendations

#### **6. Compliance & Governance**
> "GDPR, SOC2, HIPAA compliance."

**For URL shortener:**
- Data retention policies
- User consent management
- Right to be forgotten (delete user data)
- Audit logging

---

## 🎓 Final Interview Preparation Tips

### **For InfraCloud Interview:**

**1. Practice explaining your architecture on whiteboard**
```
Don't just say "I used Docker"
Say: "I used multi-stage Docker builds to reduce image size by 70%, 
      from 800MB to 250MB, which speeds up deployments and reduces 
      registry storage costs. The build stage uses Maven with JDK, 
      while runtime uses only JRE for security and efficiency."
```

**2. Know the "why" behind every decision**
- Why Kubernetes? (Auto-scaling, self-healing, declarative)
- Why Redis? (10-100x faster than database)
- Why multi-stage builds? (Smaller images, security)

**3. Prepare for troubleshooting questions**
```
Q: "Pod is in CrashLoopBackOff. How do you debug?"
A: 
1. kubectl describe pod <name> (check events)
2. kubectl logs <name> (check application logs)
3. kubectl logs <name> --previous (logs from crashed container)
4. kubectl exec -it <name> -- sh (if pod running)
5. Check resource limits (OOMKilled?)
6. Check liveness/readiness probes
7. Check ConfigMaps/Secrets mounted correctly
```

**4. Demonstrate scaling knowledge**
```
Be ready to draw this on whiteboard:

1 server → 10 servers → 100 servers → Global distribution

Explain at each stage:
- What changes (load balancer, database sharding, caching)
- Why it's needed (performance, availability)
- Trade-offs (complexity, cost, consistency)
```

**5. Show cost awareness**
```
Don't just architect for performance
Mention cost optimization:
- HPA scales down at night (save $$)
- Spot instances for batch jobs
- S3 lifecycle policies
- Right-sizing instances
```

---

## ✅ Summary: You're Well-Prepared!

### **What You Have:**
✅ Solid URL Shortener with production features  
✅ Docker & Docker Compose  
✅ Complete Kubernetes manifests  
✅ CI/CD pipeline  
✅ Redis caching (93% performance improvement)  
✅ Comprehensive documentation  
✅ Interview guides (Java, Redis, Docker, K8s)  

### **What You Know:**
✅ Docker (multi-stage, optimization)  
✅ Kubernetes (pods, deployments, services, HPA)  
✅ CI/CD (GitHub Actions)  
✅ Cloud platforms (AWS, Azure, GCP)  
✅ Scaling strategies (1K → 1B requests)  
✅ Monitoring (metrics, logs, tracing)  

### **Edge Cases to Study:**
📚 Service Mesh (Istio) - nice to have  
📚 Message Queues (Kafka) - nice to have  
📚 Chaos Engineering - nice to have  

---

**You're 95% ready for InfraCloud interview!** 🚀

The remaining 5% is confidence and practice. Do a few mock interviews explaining your architecture, and you'll ace it!

---

**Created:** February 12, 2026  
**Author:** Yash Korekar  
**Purpose:** Cloud, Docker, Kubernetes & CI/CD interview preparation for InfraCloud Technologies  
**Status:** PRODUCTION-READY! 🎯

