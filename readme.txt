dN/dS calculator

By Timothy Shaw
Email: gatechatl@gmail.com

Calculates dN/dS ratio by wrapping together pairwise alignment and MACS based protein alignment script. Gaps are then trimmed out before consolidating the alignments.


Original programs can be accessed here:
align0 from http://faculty.virginia.edu/wrpearson/fasta/
http://dna.publichealth.uga.edu/SharedPrograms/fasta3.tar.gz
PAML from http://web.mit.edu/6.891/www/lab/paml.html 

At the time, the class files were compiled using javac 1.6.0_26.  

To recompile the java program run
javac *.java

It's possible that the binary programs permission will need to be modified before running the pipeline. 
sh install.sh

Input file:
example.fa is an example input file.  The fasta needs to be one sequence per line. With the first sequence being the query and second being the query. See example http://asia.ensembl.org/info/data/ftp/index.html

To run the software try running
sh runDnDs.sh example.fa

You should see the following output:
Name	dN/dS	dN	dS
ENST00000397303-scaffold4954    0.0284  0.0065  0.2279
ENST00000037243-CL1575Contig1   0.1988  0.0495  0.2490

First number on the left is dN/dS ratio followed by dN and dS.

Notes:
1. Error checking hasn't been incorporated into the pipeline, so if the test example didn't work might want to try recompiling the program.
2. Some manual check on the final alignment will be idea to verify if the alignment is reasonable.

Citation:
Shaw TI, Srivastava A, Chou WC, Liu L, Hawkinson A, Glenn TC, Adams R, Schountz T. Transcriptome Sequencing and Annotation for the Jamaican Fruit Bat (Artibeus jamaicensis). PLoS One. 2012;7(11):e48472. doi: 10.1371/journal.pone.0048472.
