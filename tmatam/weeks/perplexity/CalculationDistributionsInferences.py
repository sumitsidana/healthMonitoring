'''
Created on Aug 24, 2015

@author: sumit
'''
#!/usr/bin/python

import sys
from operator import itemgetter

def main(): 
    filename = sys.argv[1]
    infile = file(filename, "r")

    Y = 2
    Z = 0
    A = 0
    count = {}
    count0 = {}
    countA = {}
    countA0 = {}
    countB = {}
    countBY = {}
    countY = {}

    for line in infile:
        tokens = line.split()
        id = int(tokens.pop(0))
        a = int(tokens.pop(0))

        if a not in countA:
            countA0[a] = 0
        countA0[a] += 1

        if a > A: A = a

        for token in tokens:
            parts = token.split(":")
            b = int(parts.pop())
            x = int(parts.pop())
            l = int(parts.pop())
            y = int(parts.pop())
            z = int(parts.pop())
            word = ":".join(parts)

            if b == 0:
                if l == 1 and x == 1:
                    if y not in countBY:
                        countBY[y] = {}
                    if word not in countBY[y]:
                        countBY[y][word] = 0
                    countBY[y][word] += 1
                else:
                    if word not in countB:
                        countB[word] = 0
                    countB[word] += 1
            else:
                if y > Y: Y = y
                if z > Z: Z = z

                if l == 0: x = 0

                if l == 0:
                    if z not in count:
                        count[z] = {}
                        count0[z] = 0
                    count0[z] += 1
                    if word not in count[z]:
                        count[z][word] = 0
                    count[z][word] += 1
                else:
                    if x == 0:    
                        if a not in countA:
                            countA[a] = {}
                        if word not in countA[a]:
                            countA[a][word] = 0
                        countA[a][word] += 1
                    else:
                        if y not in countY:
                            countY[y] = {}
                        if a not in countY[y]:
                            countY[y][a] = {}
                        if word not in countY[y][a]:
                            countY[y][a][word] = 0
                        countY[y][a][word] += 1

    infile.close()

    #Z += 1
    A += 1

#     print "Background"
    sys.stdout.write("Background:")
    words = sorted(countB.items(), key=itemgetter(1), reverse=True)

    w = 0
    for word, v in words:
        sys.stdout.write(word+" "+str(v)+",")
#         print word+" "+str(v) 
        w += 1
#         if w >= 50: break
#     print "Background 0"
#     sys.stdout.write("Background 0")
    if 0 not in countBY: countBY[0] = {}
    words = sorted(countBY[0].items(), key=itemgetter(1), reverse=True)
    w = 0
    for word, v in words:
        sys.stdout.write(word+" "+str(v)+",")
#         print word+" "+str(v)
        w += 1
#         if w >= 20: break
#     sys.stdout.write("Background 1")    
#     print "Background 1"
    if 1 not in countBY: countBY[1] = {}
    words = sorted(countBY[1].items(), key=itemgetter(1), reverse=True)
    w = 0
    for word, v in words:
        sys.stdout.write(word+" "+str(v)+",")
#         print word+" "+str(v)
        w += 1
#         if w >= 20: break
#     print "\n"
    sys.stdout.write("\n")

    for z in range(-1,Z):
        if z == -1: sys.stdout.write("Background"+":")
#         if z == -1: print "Background"
        else:
            if z not in count0: continue
            sys.stdout.write("Topic %d (%d)" % (z+1,count0[z])+":")
#             sys.stdout.write("Topic %d" % (z+1)+":")
#             print "Topic %d (%d)" % (z+1,count0[z])

        w = 0
        if (z not in count):
            words = {}
        else:
            words = sorted(count[z].items(), key=itemgetter(1), reverse=True)

        for word, v in words:
            sys.stdout.write(word+" "+str(v)+",")
#             print word+" "+str(v) 
#             w += 1
#             if w >= 20: break
#         print "\n"
        sys.stdout.write("\n")
        

    for z in range(0,A):
#         if z == -1: print "Background"
        if z == -1: sys.stdout.write("Background:")
        else: 
            if z not in countA0: continue
#             print "Ailment %d (%d)" % (z+1,countA0[z])
            sys.stdout.write("Ailment %d (%d)" % (z+1,countA0[z])+":")

        w = 0
        if (z not in countA):
            words = {}
        else:
            words = sorted(countA[z].items(), key=itemgetter(1), reverse=True)

        for word, v in words:
            sys.stdout.write(word+" "+str(v)+",")
#             print word+" "+str(v)  
#             w += 1
#             if w >= 20: break

        y = 0
#         print '*Symptoms'
#         sys.stdout.write("*Symptoms")
        w = 0
        if (y not in countY or z not in countY[y]):
            words = {}
        else:
            words = sorted(countY[y][z].items(), key=itemgetter(1), reverse=True)
        for word, v in words:
            sys.stdout.write(word+" "+str(v)+",")
#             print "  "+word+" "+str(v) 
#             w += 1
#             if w >= 20: break

        y = 1
#         sys.stdout.write("*Treatments")
#         print '*Treatments'
#         w = 0
        if (y not in countY or z not in countY[y]):
            words = {}
        else:
            words = sorted(countY[y][z].items(), key=itemgetter(1), reverse=True)
        for word, v in words:
            sys.stdout.write(word+" "+str(v)+",")
#             print "  "+word+" "+str(v) 
#             w += 1
#             if w >= 20: break

#         print "\n"
        sys.stdout.write("\n")

if __name__ ==  "__main__":
    main()
