package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.volmit.fulcrum.Fulcrum;
import com.volmit.fulcrum.lang.GList;
import com.volmit.fulcrum.lang.GMap;
import com.volmit.fulcrum.lang.M;

public class ResourcePack
{
	private final PackMeta meta;
	private GMap<String, URL> copyResources;
	private GMap<String, String> writeResources;
	GList<String> oc = new GList<String>();
	GList<String> ow = new GList<String>();

	public ResourcePack()
	{
		meta = new PackMeta();
		copyResources = new GMap<String, URL>();
		writeResources = new GMap<String, String>();
		oc = new GList<String>();
		ow = new GList<String>();
	}

	public void setResource(String path, URL url)
	{
		if(path == null && url == null)
		{
			return;
		}

		if(path == null)
		{
			System.out.println("PATH IS NULL: " + url.toString());
			return;
		}

		if(url == null)
		{
			System.out.println("URL IS NULL: " + path);
			return;
		}

		oc.add(path);
		copyResources.put(path, url);
	}

	public void setResource(String path, String content)
	{
		ow.add(path);
		writeResources.put(path, content);
	}

	public PackMeta getMeta()
	{
		return meta;
	}

	public byte[] writeToArchive(File f) throws IOException, NoSuchAlgorithmException
	{
		File fx = new File(Fulcrum.instance.getDataFolder(), "temp");
		fx.mkdirs();
		writeToFolder(fx);
		f.createNewFile();

		MessageDigest d = MessageDigest.getInstance("MD5");
		FileOutputStream fos = new FileOutputStream(f);
		ZipOutputStream zos = new ZipOutputStream(fos);

		for(File i : fx.listFiles())
		{
			addToZip(d, i, fx, zos);
		}

		zos.close();
		delete(fx);
		f.setLastModified(M.ms());

		return d.digest();
	}

	private void delete(File fx)
	{
		if(fx.isDirectory())
		{
			for(File i : fx.listFiles())
			{
				delete(i);
			}
		}

		fx.delete();
	}

	private void addToZip(MessageDigest d, File file, File root, ZipOutputStream s) throws IOException
	{
		if(file.isDirectory())
		{
			for(File i : file.listFiles())
			{
				addToZip(d, i, root, s);
			}
		}

		else
		{
			String path = file.getAbsolutePath();
			String base = root.getAbsolutePath();
			String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
			ZipEntry ze = new ZipEntry(relative);
			FileInputStream fin = new FileInputStream(file);
			s.putNextEntry(ze);
			byte[] buf = new byte[1024];
			int read = 0;

			while((read = fin.read(buf)) != -1)
			{
				d.update(buf, 0, read);
				s.write(buf, 0, read);
			}

			s.closeEntry();
			fin.close();
		}
	}

	public void writeToFolder(File f) throws IOException
	{
		writePackContent(new File(f, "pack.mcmeta"), getMeta().toString());
		writeResourceToFile(getMeta().getPackIcon(), new File(f, "pack.png"));

		for(String i : oc.copy())
		{
			File destination = new File(f, "assets" + File.separator + "minecraft" + File.separator + i.replaceAll("/", Matcher.quoteReplacement(File.separator)));
			destination.getParentFile().mkdirs();
			writeResourceToFile(copyResources.get(i), destination);
		}

		for(String i : ow.copy())
		{
			File destination = new File(f, "assets" + File.separator + "minecraft" + File.separator + i.replaceAll("/", Matcher.quoteReplacement(File.separator)));
			destination.getParentFile().mkdirs();
			writePackContent(destination, writeResources.get(i));
		}
	}

	private void writeResourceToFile(URL url, File f) throws IOException
	{
		if(url == null)
		{
			System.out.println("WARNING! Resource is null: " + f.getAbsolutePath());
			return;
		}

		try
		{
			FileOutputStream fos = new FileOutputStream(f);
			InputStream in = url.openStream();
			byte[] buffer = new byte[1024];
			int read = 0;

			while((read = in.read(buffer)) != -1)
			{
				fos.write(buffer, 0, read);
			}

			fos.close();
			in.close();
		}

		catch(Exception e)
		{
			System.out.println("FAILED TO PACK RESOURCE: " + e.getMessage());
		}

	}

	private void writePackContent(File m, String content) throws IOException
	{
		m.createNewFile();
		PrintWriter pw = new PrintWriter(m);
		pw.println(content);
		pw.close();
	}
}
