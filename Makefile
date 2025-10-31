.PHONY: help run build test clean docker-up docker-down docker-logs docker-status

DOCKER_COMPOSE ?= docker compose

PROJECT_NAME := asset-management-be

ifneq (,$(wildcard .env))
	include .env
endif

MVNW := ./mvnw
ifeq ($(OS),Windows_NT)
	MVNW := mvnw.cmd
endif

export DATABASE_URL DATABASE_USERNAME DATABASE_PASSWORD SECRET_KEY SERVER_PORT
export REDIS_HOST REDIS_PORT REDIS_PASSWORD WEBSOCKET_ALLOWED_ORIGINS \
	NOTIFICATION_TOPIC NOTIFICATION_EVAL_LEAD_DAYS NOTIFICATION_EVAL_CRON \
	NOTIFICATION_EVAL_CYCLE_YEARS NOTIFICATION_EVAL_TIMEZONE \
	SWAGGER_TITLE SWAGGER_DESCRIPTION
export SWAGGER_VERSION SWAGGER_LICENSE SWAGGER_LICENSE_URL JWT_EXPIRATION
export MYSQL_DATABASE MYSQL_ROOT_PASSWORD MYSQL_IMAGE MYSQL_PORT MYSQL_CPUS \
	MYSQL_RESERVATION_CPUS MYSQL_MEM MYSQL_RESERVATION_MEM \
	MYSQL_INNODB_BUFFER_POOL_SIZE MYSQL_INNODB_LOG_BUFFER_SIZE \
	MYSQL_MAX_CONNECTIONS REDIS_IMAGE REDIS_CPUS REDIS_MEM \
	REDIS_RESERVATION_CPUS REDIS_RESERVATION_MEM REDIS_APPENDONLY

help:
	@echo "Useful targets for $(PROJECT_NAME):"
	@echo "  make run           # Run the Spring Boot application with Maven wrapper"
	@echo "  make build         # Build the project and package the jar"
	@echo "  make test          # Run unit tests"
	@echo "  make clean         # Remove Maven build artifacts"
	@echo "  make docker-up     # Start MySQL & Redis via Docker Compose"
	@echo "  make docker-down   # Stop and remove Compose services"
	@echo "  make docker-logs   # Follow logs from Compose services"
	@echo "  make docker-status # Show status of Compose services"

run:
	$(MVNW) spring-boot:run

build:
	$(MVNW) clean package

test:
	$(MVNW) test

clean:
	$(MVNW) clean

docker-up:
	$(DOCKER_COMPOSE) up -d

docker-down:
	$(DOCKER_COMPOSE) down

docker-logs:
	$(DOCKER_COMPOSE) logs -f

docker-status:
	$(DOCKER_COMPOSE) ps
