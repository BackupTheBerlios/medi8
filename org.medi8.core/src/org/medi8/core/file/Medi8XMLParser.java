/*
 * Created on Nov 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.core.file;

import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.Provenance;
import org.medi8.internal.core.model.SelectionClip;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.Track;
import org.medi8.internal.core.model.VideoTrack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Medi8XMLParser extends DefaultHandler
{
	// The current Sequence.
	Sequence currentSequence;
	// The current Track.
	VideoTrack currentTrack;
	// Start time of the current clip.
	Time startTime;
	// The clip we just parsed.
	Clip currentClip;
	
	// Some state for parsing a selection.
	Time currentStart;
	Time currentEnd;

	public Medi8XMLParser()
	{
	}
	
	public Sequence getSequence ()
	{
		return currentSequence;
	}

	public void endElement(String name)
	{
		if ("track".equals(name))
		{
			currentSequence.addTrack(currentTrack);
			currentTrack = null;
		}
		else if ("clip".equals(name))
		{
			currentTrack.addClip(startTime,currentClip);
			startTime = null;
			currentClip = null;
		}
		else if ("selection".equals(name))
		{
			currentClip = new SelectionClip(currentClip, currentStart, currentEnd);
			currentStart = null;
			currentEnd = null;
		}
	}

	public void endElement(String uri, String name, String qName)
			throws SAXException
	{
		if ("".equals(uri))
			endElement(qName);
		else
			endElement(name);
	}
	
	public void startElement(String name, Attributes attrs)
	{
		if ("sequence".equals(name))
			currentSequence = new Sequence ();
		else if ("track".equals(name))
			currentTrack = new VideoTrack ();
		else if ("clip".equals(name))
			startTime = new Time(Double.parseDouble(attrs.getValue("time")));
		else if ("file".equals(name))
		{
			String filename = attrs.getValue("name");
			Provenance where = new Provenance (filename);
			Time length = new Time(Double.parseDouble(attrs.getValue("length")));
			String subpart = attrs.getValue("subpart");
			currentClip = new FileClip(where, length, filename, subpart);
		}
		else if ("selection".equals(name))
		{
			currentStart = new Time(Double.parseDouble(attrs.getValue("start")));
			currentEnd = new Time(Double.parseDouble(attrs.getValue("end")));
		}
	}

	public void startElement(String uri, String name, String qName,
			Attributes attrs) throws SAXException
	{
		if ("".equals(uri))
			startElement(qName, attrs);
		else
			startElement(name, attrs);
	}
}
