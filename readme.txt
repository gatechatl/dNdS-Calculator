dN/dS calculator

By Timothy Shaw
Email: gatechatl@gmail.com

To calculate dN/dS ratio a simple java program wraps together a pairwise alignment and MACS protein alignment.  We then trim out the gaps before performing the 

For setup:
Because some of the program are precompiled in x64 Redhat server so for other operating system might not work as smoothly.

For recompiling the programs:
align0 from http://faculty.virginia.edu/wrpearson/fasta/
Or you can recompile from my following tarball
http://dna.publichealth.uga.edu/SharedPrograms/fasta3.tar.gz

PAML from http://web.mit.edu/6.891/www/lab/paml.html 

We used javac 1.6.0_26 for compiling our java program.  So if you have a lower version of java the java program might need to be recompiled.

To recompile the java program run
javac *.java

After unzipping the software you might need to modify the binary executable's permission, to do so try.
sh install.sh

Input file:
example.fa is an example input file.  The format is a fasta file with the sequence being one line.  The query and target sequence is followed one after another.  Generally the target sequence I will use the cDNA sequences for example http://asia.ensembl.org/info/data/ftp/index.html

To try running the software try running
sh runDnDs.sh example.fa

You should see the following output:
Name	dN/dS	dN	dS
ENST00000397303-scaffold4954    0.0284  0.0065  0.2279
ENST00000037243-CL1575Contig1   0.1988  0.0495  0.2490

First number on the left is dN/dS ratio followed by dN and dS on the right.

If you didn't see those output then try checking whether the programs need to be recompiled.

Note: Because this is an automated pairwise alignment and dN/dS calculator not all alignment will be good.

Citation:
Shaw TI, Srivastava A, Chou WC, Liu L, Hawkinson A, Glenn TC, Adams R, Schountz T. Transcriptome Sequencing and Annotation for the Jamaican Fruit Bat (Artibeus jamaicensis). PLoS One. 2012;7(11):e48472. doi: 10.1371/journal.pone.0048472.
