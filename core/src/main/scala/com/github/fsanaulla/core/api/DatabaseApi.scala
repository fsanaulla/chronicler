package com.github.fsanaulla.core.api

import com.github.fsanaulla.core.io.{ReadOperations, WriteOperations}

trait DatabaseApi[E] extends ReadOperations with WriteOperations[E]
