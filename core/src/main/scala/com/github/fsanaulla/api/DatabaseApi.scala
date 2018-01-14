package com.github.fsanaulla.api

import com.github.fsanaulla.io.{ReadOperations, WriteOperations}

trait DatabaseApi[E] extends ReadOperations with WriteOperations[E]
