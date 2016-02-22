package com.kbsmc.webcasi.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClassClone {
	public Object clone(Object originalObject) {
		Object clonedObject = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			objectOutputStream.writeObject(originalObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			clonedObject = objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clonedObject;
	}
}
