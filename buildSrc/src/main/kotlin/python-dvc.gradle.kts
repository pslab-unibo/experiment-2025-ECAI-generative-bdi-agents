val pythonEnv = PythonEnvironment(project)
project.extensions.extraProperties["pythonEnv"] = pythonEnv

pythonEnv.registerTasks()
