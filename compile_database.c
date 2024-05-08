#include <dirent.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static char cmd_add[] = "./md --addMolecule testcases/molecules/";

int main(void)
{
    DIR *molecule_directory;
    molecule_directory = opendir("testcases/molecules/");

    struct dirent *molecule;

    char cmd_buffer[80];
    strcpy(cmd_buffer, cmd_add);

    if (molecule_directory)
    {
        while ((molecule = readdir(molecule_directory)) != NULL)
        {
            if (molecule->d_type == DT_REG)
            {
                strcpy(&cmd_buffer[sizeof(cmd_add) - 1], molecule->d_name);
                system(cmd_buffer);
            }
        }
        closedir(molecule_directory);
    }
    return 0;
}
