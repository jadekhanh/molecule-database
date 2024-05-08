benchmark: benchmark.c
	gcc -g -O3 -Wall -o benchmark.bin benchmark.c

database: compile_database.c
	gcc -g -O3 -Wall -o compile_database.bin compile_database.c