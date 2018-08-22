/**
 * ﻿Copyright 2015-2018 Valery Silaev (http://vsilaev.com)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.

 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.tascalate.async.xpi;

import java.util.concurrent.CompletionStage;

import net.tascalate.async.Generator;
import net.tascalate.concurrent.Promise;
import net.tascalate.concurrent.Promises;

import net.tascalate.javaflow.util.SuspendableStream;

class PromisesGeneratorImpl <T> implements PromisesGenerator<T> {
    
    private final Generator<T> delegate;
    
    PromisesGeneratorImpl(Generator<T> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Promise<T> next() {
        return next(NO_PARAM);
    }
    
    @Override
    public Promise<T> next(Object producerParam) {
        CompletionStage<T> original = NO_PARAM == producerParam ? 
            delegate.next() : delegate.next(producerParam);
        return null == original ? null : Promises.from(original);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public SuspendableStream<Promise<T>> stream() {
        return delegate.stream().map(Promises::from);
    }
    
    @Override
    public String toString() {
        return String.format("%s[delegate=%s]", PromisesGenerator.class.getSimpleName(), delegate);
    }    
   
    private static final Object NO_PARAM = new Object();
}
