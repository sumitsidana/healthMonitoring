'''
Created on Sep 11, 2015

@author: sumit
'''
#!/usr/bin/python

import sys
from operator import itemgetter

def main(): 
    filename = sys.argv[1]
    infile = file(filename, "r")

    Z = 0
    count = {}
    countB = {}

    for line in infile:
        tokens = line.split()
        if len(tokens) < 3: continue

        id = tokens.pop(0)
        #garbage = tokens.pop(0)

        for token in tokens:
            parts = token.split(":")
            x = int(parts.pop())
            z = int(parts.pop())
            word = ":".join(parts)
  
            if x == 1:
                if z not in count:
                    count[z] = {}
                if word not in count[z]:
                    count[z][word] = 0
                count[z][word] += 1

                if z > Z: Z = z
            else:
                if word not in countB:
                    countB[word] = 0
                countB[word] += 1
    infile.close()
    Z += 1

#     print "Background\n"
    sys.stdout.write("Background:")
#     w = 0
    words = sorted(countB.items(), key=itemgetter(1), reverse=True)
    for word, v in words:
        sys.stdout.write(word+" "+str(v)+"||#||")
#         print word, v
#         w += 1
#         if w >= 30: break
    sys.stdout.write("\n")
#     print "\n"

    for z in range(Z):
#         sys.stdout.write("Topic %d\n" % (z+0))
#         sys.stdout.write("Topic %d:" % (z+0))
        sys.stdout.write("Topic %d:" % (z+1))
#         print "Topic %d\n" % (z+0)
#         w = 0
        if z not in count: count[z] = {}
        words = sorted(count[z].items(), key=itemgetter(1), reverse=True)
        for word, v in words:
            sys.stdout.write(word+" "+str(v)+"||#||")
#             print word, v
#             w += 1
#             if w >= 40: break
        sys.stdout.write("\n")
#         print "\n"

if __name__ ==  "__main__":
  main()
