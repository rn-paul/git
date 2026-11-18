package repository;

import domain.Entity;
import domain.validators.Validator;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;



public abstract class AbstractFileRepository< ID, E extends Entity<ID>> extends InMemoryRepository<ID,E>{
    private String filename;
    public AbstractFileRepository(Validator<E> validator,String filename){
        super(validator);
        this.filename=filename;
        loaddata();
    }
    protected abstract E extractEntity(List<String> attributes);
    protected abstract String createEntityAsString(E Entity);
    private void loaddata(){
        Path path =Paths.get(filename);
        if(!Files.exists(path)){
            return;
        }
        try(Stream<String> stream=Files.lines(path)){
            List<String> lines=stream.collect(Collectors.toList());
            IntStream.range(0,lines.size()).forEach(i->{
                try{
                    List<String> attrs=Arrays.asList(lines.get(i).split(";"));
                    E entity=extractEntity(attrs);
                    super.save(entity);
                }catch(Exception parseException){
                    System.err.println("skipping corrupted line"+(i+1)+" in file "+(filename)+":"+(parseException.getMessage()));

                }
            });
        }catch(IOException e){
            throw new RepositoryException("error reading from file "+ filename,e);
        }
    }
    private void writeToFile(){
        Path path=Paths.get(filename);
        try{
            List<String> lines=StreamSupport.stream(super.findAll().spliterator(),false)
                    .map(this::createEntityAsString)
                    .collect(Collectors.toList());
            Files.write(path,lines);
        }catch(IOException e){
            throw new RepositoryException("error writing to file "+filename,e);
        }
    }
    @Override
    public Optional<E> save(E entity){
        Optional<E> optional=super.save(entity);
        if(optional.isEmpty()){
            writeToFile();
        }
        return optional;
    }
    @Override
    public Optional<E> delete(ID id){
        Optional<E> optional=super.delete(id);
        if(optional.isPresent())writeToFile();
        return optional;
    }

}
