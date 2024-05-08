#include <dirent.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <time.h>

static char cmd_find[] = "./md --findMolecule testcases/new_molecules/";

int main(void)
{
    clock_t start = clock();

    DIR *molecule_directory;
    molecule_directory = opendir("testcases/new_molecules/");

    struct dirent *molecule;

    char cmd_buffer[80];
    strcpy(cmd_buffer, cmd_find);

    if (molecule_directory)
    {
        while ((molecule = readdir(molecule_directory)) != NULL)
        {
            if (molecule->d_type == DT_REG)
            {
                strcpy(&cmd_buffer[sizeof(cmd_find) - 1], molecule->d_name);
                system(cmd_buffer);
            }
        }
        closedir(molecule_directory);
    }

    clock_t diff = clock() - start;
    int m_sec = diff * 1000 / CLOCKS_PER_SEC;
    printf("time taken %d seconds %d milliseconds\n", m_sec / 1000, m_sec % 1000);
    return 0;
}
