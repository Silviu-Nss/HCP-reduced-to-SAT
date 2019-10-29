This program does a polinomial reduction of HCP to SAT. It receives as input a file which contains details of a graph, having on the first line the number of nodes (N), and the next N lines represent the edges.

Symbols used:

    xi-j = 1 if the edge from i to j is in the chosen path, 0 otherwise
    ai-j = 1 if the shortest path from 1 to j in the chosen path has length i, 0 otherwise
