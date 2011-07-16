import json
import fileinput
import sys
import lib.lorem_ipsum

count = 0
for i in range(1, 8000000):
	output = {}
	output['id'] = i
	output['shortStringAttribute'] = lib.lorem_ipsum.words(5, False)
	output['longStringAttribute'] = lib.lorem_ipsum.paragraph()
	output['intNumber'] = len(output['longStringAttribute'])
	output['trueOrFalse'] = True
	print json.dumps(output)

	count = count + 1
