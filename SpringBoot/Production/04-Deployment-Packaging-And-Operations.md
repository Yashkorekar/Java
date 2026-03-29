# Deployment, Packaging, and Operations — Practical Spring Boot Notes

## Packaging model
Most modern Spring Boot services are packaged as executable JARs.

Why JAR is common:
- embedded server
- easy containerization
- simple deployment model

WAR still exists, but it is less common in greenfield Boot services.

## Externalized configuration in deployment
Common runtime config sources:
- environment variables
- mounted config files
- secrets managers
- command line arguments

Good answer:
- Build once, deploy the same artifact to different environments, and change behavior through configuration.

## Docker and buildpacks
Common options:
- Dockerfile
- Cloud Native Buildpacks via `bootBuildImage`

Interview angle:
- Buildpacks are convenient and secure defaults for many teams.
- Dockerfiles give more control.

## Graceful shutdown
What it means:
- stop accepting new work, finish or fail in-flight work cleanly, release resources.

Why it matters:
- container restarts and rolling deployments happen all the time.

## Readiness and liveness in deployments
- liveness answers whether the process should be restarted.
- readiness answers whether the instance should receive traffic.

Strong answer:
- A service can be alive but not ready.

## Database migrations in release flow
Best practice:
- manage schema changes with Flyway or Liquibase as part of deployment discipline.

Trap:
- Relying on ad hoc manual DDL changes makes releases fragile.

## Startup and warmup thinking
Questions to consider:
- does the service need caches warmed?
- are external dependencies available?
- is startup time acceptable for autoscaling?

## Native image and AOT
Why it comes up:
- people care about startup time and memory in cloud environments.

Safe answer:
- Native image can be useful, but it changes build complexity and runtime assumptions, so you evaluate it case by case.

## Operational checklist
- health endpoints exposed correctly
- metrics exported
- logs structured and centralized
- secrets not baked into images
- environment-specific config externalized
- database migrations controlled
- graceful shutdown configured

## Common deployment mistakes
- bundling secrets inside the artifact or image
- exposing every actuator endpoint publicly
- assuming local profile config matches production
- ignoring readiness during startup spikes
- skipping schema migration discipline