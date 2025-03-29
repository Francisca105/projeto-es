run_frontend:
	# docker compose build
	docker compose up frontend

run_tests_e2e:
	docker compose down
	docker compose build
	docker compose up -d postgres
	@$(MAKE) psql_setup
	docker compose up e2e-run

git_tag_final:
	git tag entrega2

git_remove_final_tag:
	git tag -d entrega2
	git push origin --delete entrega2

git_tags_push:
	git push --tags

psql_setup:
	@$(MAKE) pg_drop
	@$(MAKE) pg_create

export PGPASSWORD=password

pg_drop:
	dropdb hedb -h 127.0.0.1 -p 5433 -U engineer

pg_create:
	createdb hedb -h 127.0.0.1 -p 5433 -U engineer
	
pg_populate:
	psql hedb -h 127.0.0.1 -p 5433 -U engineer -X < data/database-initialization/hedb-2025-create-data.sql
	psql hedb -h 127.0.0.1 -p 5433 -U engineer -X < data/database-initialization/hedb-create-assessments.sql
	psql hedb -h 127.0.0.1 -p 5433 -U engineer -X < data/database-initialization/hedb-create-enrollments.sql
	psql hedb -h 127.0.0.1 -p 5433 -U engineer -X < data/database-initialization/hedb-create-participations.sql
