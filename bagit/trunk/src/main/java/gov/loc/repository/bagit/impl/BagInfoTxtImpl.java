package gov.loc.repository.bagit.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

public class BagInfoTxtImpl extends AbstractNameValueBagFile implements BagInfoTxt {

	public static final String SOURCE_ORGANIZATION = "Source-Organization";
	public static final String ORGANIZATION_ADDRESS = "Organization-Address";
	public static final String CONTACT_NAME = "Contact-Name";
	public static final String CONTACT_PHONE = "Contact-Phone";
	public static final String CONTACT_EMAIL = "Contact-Email";
	public static final String EXTERNAL_DESCRIPTION = "External-Description";
	public static final String BAGGING_DATE = "Bagging-Date";
	public static final String EXTERNAL_IDENTIFIER = "External-Identifier";
	public static final String BAG_SIZE = "Bag-Size";
	public static final String PAYLOAD_OXUM = "Payload-Oxum";
	public static final String BAG_GROUP_IDENTIFIER = "Bag-Group-Identifier";
	public static final String BAG_COUNT = "Bag-Count";
	public static final String INTERNAL_SENDER_IDENTIFIER = "Internal-Sender-Identifier";
	public static final String INTERNAL_SENDER_DESCRIPTION = "Internal-Sender-Description";
	private static final String STREAM_COUNT_PART = "Stream Count";
	private static final String OCTET_COUNT_PART = "Octet Count";
	
	
	private static final long serialVersionUID = 1L;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public BagInfoTxtImpl(BagFile bagFile, BagConstants bagConstants) {
		super(bagConstants.getBagInfoTxt(), bagFile, bagConstants.getBagEncoding());
	}
	
	public BagInfoTxtImpl(BagConstants bagConstants) {
		super(bagConstants.getBagInfoTxt(), bagConstants.getBagEncoding());
			
	}

	@Override
	public String getBagCount() {
		return this.getCaseInsensitive(BAG_COUNT);
	}

	@Override
	public Integer getTotalBagsInGroup() throws ParseException {
		String bagCount = this.getBagCount(); 
		if (bagCount == null) {
			return null;
		}
		int pos = bagCount.indexOf(" of ");
		if (pos == -1 || pos+4 >= bagCount.length()) {
			throw new ParseException("Bag-Count not structured N of T",0);
		}
		String totalBags = bagCount.substring(pos+4);		
		if ("?".equals(totalBags)) {
			return UNKNOWN_TOTAL_BAGS_IN_GROUP;
		}
		try {
			return Integer.parseInt(totalBags);
		} catch(NumberFormatException ex) {
			throw new ParseException("Total Bags in Group is not an integer: " + totalBags, 0);
		}
	}
	
	@Override
	public Integer getBagInGroup() throws ParseException {
		String bagCount = this.getBagCount(); 
		if (bagCount == null) {
			return null;
		}
		int pos = bagCount.indexOf(" of ");
		if (pos == -1 || pos-1 < 0) {
			throw new ParseException("Bag-Count not structured N of T",0);
		}
		
		String bagInGroup = bagCount.substring(0, pos);
		try {
			return Integer.parseInt(bagInGroup);
		} catch (NumberFormatException ex) {
			throw new ParseException("Bag in Group is not an integer: " + bagInGroup, 0);
		}
	}
	
	@Override
	public String getBagGroupIdentifier() {
		return this.getCaseInsensitive(BAG_GROUP_IDENTIFIER);
	}

	@Override
	public String getBagSize() {
		return this.getCaseInsensitive(BAG_SIZE);
	}

	@Override
	public void generateBagSize(Bag bag) {
		this.setBagSize(BagHelper.generateBagSize(bag));		
	}
	
	@Override
	public String getBaggingDate() {
		return this.getCaseInsensitive(BAGGING_DATE);
	}

	@Override
	public Date getBaggingDateObj() throws ParseException {
		String baggingDate = this.getBaggingDate();
		if (baggingDate == null) {
			return null;
		}
		return this.dateFormat.parse(baggingDate);
	}
	
	@Override
	public String getContactEmail() {
		return this.getCaseInsensitive(CONTACT_EMAIL);
	}

	@Override
	public String getContactName() {
		return this.getCaseInsensitive(CONTACT_NAME);
	}

	@Override
	public String getContactPhone() {
		return this.getCaseInsensitive(CONTACT_PHONE);
	}

	@Override
	public String getExternalDescription() {
		return this.getCaseInsensitive(EXTERNAL_DESCRIPTION);
	}

	@Override
	public String getExternalIdentifier() {
		return this.getCaseInsensitive(EXTERNAL_IDENTIFIER);
	}

	@Override
	public String getInternalSenderDescription() {
		return this.getCaseInsensitive(INTERNAL_SENDER_DESCRIPTION);
	}

	@Override
	public String getInternalSenderIdentifier() {
		return this.getCaseInsensitive(INTERNAL_SENDER_IDENTIFIER);
	}

	@Override
	public String getOrganizationAddress() {
		return this.getCaseInsensitive(ORGANIZATION_ADDRESS);
	}

	@Override
	public String getPayloadOxum() {
		return this.getCaseInsensitive(PAYLOAD_OXUM);
	}

	@Override
	public Long getOctetCount() throws ParseException {
		return this.getOxumPart(OCTET_COUNT_PART);
	}
	
	@Override
	public Long getStreamCount() throws ParseException {
		return this.getOxumPart(STREAM_COUNT_PART);
	}
	
	private Long getOxumPart(String part) throws ParseException {
		String oxum = this.getPayloadOxum();
		if (oxum == null) {
			return null;
		}
		String[] split = oxum.split("\\.");
		if (split.length != 2) {
			throw new ParseException("Payload-Oxum is not OctetCount.StreamCount",0);
		}
		
		int pos = 0;
		if (STREAM_COUNT_PART.equals(part)) {
			pos = 1;
		}
		try {	
			return Long.parseLong(split[pos]);
		} catch (NumberFormatException ex) {
			throw new ParseException(part + " is not an integer: " + split[pos], 0);
		}
		
	}
	
	@Override
	public String getSourceOrganization() {
		return this.getCaseInsensitive(SOURCE_ORGANIZATION);
	}

	@Override
	public void setBagCount(String bagCount) {
		this.put(BAG_COUNT, bagCount);		
	}

	@Override
	public void setBagCount(int bagInGroup, int totalBagsInGroup) {
		String totalBags = Integer.toString(totalBagsInGroup);
		if (totalBagsInGroup == UNKNOWN_TOTAL_BAGS_IN_GROUP) {
			totalBags = UNKNOWN_TOTAL_BAGS_IN_GROUP_MARKER;
		}
		this.setBagCount(MessageFormat.format("{0} of {1}", Integer.toString(bagInGroup), totalBags));
		
	}
	
	@Override
	public void setBagGroupIdentifier(String bagGroupIdentifier) {
		this.put(BAG_GROUP_IDENTIFIER, bagGroupIdentifier);
		
	}

	@Override
	public void setBagSize(String bagSize) {
		this.put(BAG_SIZE, bagSize);
		
	}

	@Override
	public void setBaggingDate(String baggingDate) {
		this.put(BAGGING_DATE, baggingDate);		
	}

	@Override
	public void setBaggingDate(Date date) {
		String dateString = null;
		if (date != null) {
			dateString = this.dateFormat.format(date);
		}
		this.setBaggingDate(dateString);
	}
	
	@Override
	public void setBaggingDate(int year, int month, int day) {
		try {
			this.setBaggingDate(this.dateFormat.parse(MessageFormat.format("{0}-{1}-{2}", Integer.toString(year), month, day)));
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	@Override
	public void setContactEmail(String contactEmail) {
		this.put(CONTACT_EMAIL, contactEmail);
		
	}

	@Override
	public void setContactName(String contactName) {
		this.put(CONTACT_NAME, contactName);
		
	}

	@Override
	public void setContactPhone(String contactPhone) {
		this.put(CONTACT_PHONE, contactPhone);
		
	}

	@Override
	public void setExternalDescription(String externalDescription) {
		this.put(EXTERNAL_DESCRIPTION, externalDescription);
		
	}

	@Override
	public void setExternalIdentifier(String externalIdentifier) {
		this.put(EXTERNAL_IDENTIFIER, externalIdentifier);
		
	}

	@Override
	public void setInternalSenderDescription(String internalSenderDescription) {
		this.put(INTERNAL_SENDER_DESCRIPTION, internalSenderDescription);
		
	}

	@Override
	public void setInternalSenderIdentifier(String internalSenderIdentifier) {
		this.put(INTERNAL_SENDER_IDENTIFIER, internalSenderIdentifier);
		
	}

	@Override
	public void setOrganizationAddress(String organizationAddress) {
		this.put(ORGANIZATION_ADDRESS, organizationAddress);		
	}

	@Override
	public void setPayloadOxum(String payloadOxsum) {
		this.put(PAYLOAD_OXUM, payloadOxsum);		
	}
	
	@Override
	public void setPayloadOxum(long octetCount, long streamCount) {
		this.setPayloadOxum(Long.toString(octetCount) + "." + Long.toString(streamCount));
		
	}

	@Override
	public void generatePayloadOxum(Bag bag) {
		this.setPayloadOxum(BagHelper.generatePayloadOctetCount(bag), bag.getPayloadFiles().size());		
	}
	
	@Override
	public void setSourceOrganization(String sourceOrganization) {
		this.put(SOURCE_ORGANIZATION, sourceOrganization);		
	}
	
	@Override
	public String getType() {
		return BagInfoTxt.TYPE;
	}
}
