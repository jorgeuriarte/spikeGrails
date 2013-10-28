def branch = 'svn info'.execute() | 'grep ^URL'.execute()
def output = branch.text
println output.replaceAll(/.+repos\//, '')
def match = output =~ /.+repos\/(.+)$/
println match[0][1]
