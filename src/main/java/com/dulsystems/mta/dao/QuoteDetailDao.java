package com.dulsystems.mta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.dulsystems.mta.bean.QuoteBean;
import com.dulsystems.mta.bean.QuoteDetailBean;
import com.dulsystems.mta.bean.RequestBean;
import com.dulsystems.mta.bean.VehicleBean;
import com.dulsystems.mta.dao.mapper.QuoteDetailMapper;
import com.dulsystems.mta.dao.mapper.QuoteMapper;
import com.dulsystems.mta.util.Queries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class QuoteDetailDao implements IQuoteDetailDao{

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private NamedParameterJdbcTemplate  namedParameterJdbcTemplate ;
	
	//DAO FOR QUOTE
	@Override
	public QuoteBean searchQuoteById(Integer id) {
		try {
			QuoteBean qb = jdbcTemplate.queryForObject(Queries.Q_QUOTES_SEARCH_BY_ID, new QuoteMapper(), id);
			return qb;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@Override
	public QuoteBean searchLastQuoteCreated(RequestBean request, Integer vehicleId) {
		try {
			System.out.println("ID DEL AUTO: " + vehicleId);
			System.out.println("FECHA DE ENTREGA: " + request.getQuoteDeadline());
			QuoteBean qb = jdbcTemplate.queryForObject(Queries.Q_QUOTES_SEARCH_LAST_QUOTE_CREATED, new QuoteMapper(), new Object[] { vehicleId, request.getQuoteOrderDate(), request.getQuoteDeadline() } );
			return qb;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@Override
	public List<QuoteBean> searchVehicleQuotesByVehicleId(Integer id){
		try {
			List<QuoteBean> lqb = jdbcTemplate.query(Queries.Q_QUOTES_SEARCH_VEHICLE_QUOTES, new QuoteMapper(), id);
			return lqb;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}

	@Override
	public boolean executeSaveQuote(RequestBean request, VehicleBean vb) {
		boolean bin = false;
		int result = jdbcTemplate.update(Queries.Q_QUOTES_SAVE, new Object[] { vb.getVehicleId(),request.getQuoteOrderDate(),request.getQuoteDeadline(),request.getQuoteStatusVehicle(),request.getQuotePaymentMethod(),request.getQuotePaymentStatus(),request.getQuoteAdvancePayment(),request.getQuoteRequireInvoice() });
        if (result > 0) {
            bin = true;
        }
		return bin;
	}

	@Override
	public boolean executeUpdateQuoteById(RequestBean request, VehicleBean vb) {
		boolean bin = false;
		int result = jdbcTemplate.update(Queries.Q_QUOTES_UPDATE_BY_ID, new Object[] { vb.getVehicleId(),request.getQuoteOrderDate(),request.getQuoteDeadline(),request.getQuoteStatusVehicle(),request.getQuotePaymentMethod(),request.getQuotePaymentStatus(),request.getQuoteAdvancePayment(),request.getQuoteRequireInvoice(), request.getQuoteId() });
        if (result > 0) {
            bin = true;
        }
		return bin;
	}

	@Override
	public boolean removeQuoteById(Integer id) {
		boolean bin = false;
		int result = jdbcTemplate.update(Queries.Q_QUOTES_REMOVE_BY_ID, id);
		if (result > 0) {
            bin = true;
        }
		return bin;
	}
	
	//DAO FOR DETAILS OF A QUOTE
	@Override
	public List<QuoteDetailBean> searchQuoteDetailsByQuoteId(Integer quoteId) {
		try {
			List<QuoteDetailBean> lqdb = jdbcTemplate.query(Queries.Q_QUOTE_DETAILS_SEARCH_BY_ID,  new QuoteDetailMapper(), quoteId);
			System.out.println(lqdb);
			return lqdb;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}

	@Override
	public int[] executeSaveQuoteDetails(RequestBean request, List<QuoteDetailBean> lqdb) {
		return jdbcTemplate.batchUpdate(
				Queries.Q_QUOTE_DETAILS_SAVE,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						QuoteDetailBean qdb = lqdb.get(i);
						ps.setInt(1, qdb.getQuoteDetailIdFk());
						ps.setInt(2, qdb.getQuoteDetailMecId());
						ps.setString(3, qdb.getQuoteDetailLabour());
						ps.setLong(4, qdb.getQuoteDetailAmount().longValue());
					}
					public int getBatchSize() {
						return lqdb.size();
					}
				});
	}
	/*public boolean executeSaveQuoteDetail(RequestBean request, QuoteBean qb, List<QuoteDetailBean> lqdb) {
		boolean bin = false;
		int result = jdbcTemplate.update(Queries.Q_QUOTE_DETAILS_SAVE, new Object[] { qb.getQuoteId(),request.getQuoteDetailMecId(),request.getQuoteDetailLabour(),request.getQuoteDetailAmount() });
        if (result > 0) {
            bin = true;
        }
		return bin;
	}*/

	@Override
	public int[] executeUpdateQuoteDetailsByDetailId(RequestBean request, List<QuoteDetailBean> lqdbForUpdate) {
		return jdbcTemplate.batchUpdate(
				Queries.Q_QUOTE_DETAILS_UPDATE_BY_DETAIL_ID,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						QuoteDetailBean qdb = lqdbForUpdate.get(i);
						ps.setInt(1, qdb.getQuoteDetailIdFk());
						ps.setInt(2, qdb.getQuoteDetailMecId());
						ps.setString(3, qdb.getQuoteDetailLabour());
						ps.setLong(4, qdb.getQuoteDetailAmount().longValue());
						ps.setInt(5, qdb.getQuoteDetailId());
					}
					public int getBatchSize() {
						return lqdbForUpdate.size();
					}
				});
	}
	
	@Override
	public int[] removeQuoteDetailsByDetailId(int[] lqdbForDelete/*, String finalQuery*/) {
		int[] argTypes = { Types.INTEGER };
		List<Object[]> batchArgs = new ArrayList<>();
		for(int i = 0; i < lqdbForDelete.length; i++) {
			batchArgs.add(new Object[] { lqdbForDelete[i] });
		}
		return jdbcTemplate.batchUpdate(Queries.Q_QUOTE_DETAILS_REMOVE_BY_DETAIL_ID, batchArgs, argTypes);
		
		/*
		//WORKS FINE
		boolean bin = false;
		List<Integer> batchArgs = new ArrayList<>();
		for(int i = 0; i < lqdbForDelete.length; i++) {
			batchArgs.add(lqdbForDelete[i]);
		}
		
		int result = jdbcTemplate.update(finalQuery, batchArgs.toArray());
		if (result > 0) {
            bin = true;
        }
		return bin;*/
		
		/*
		//WORKS FINE
		List<Integer> params = new ArrayList<>();
		for(int i = 0; i < lqdbForDelete.length; i++) {
			params.add(lqdbForDelete[i]);
		}
		Map namedParameters = Collections.singletonMap("detailIds", params);
		return namedParameterJdbcTemplate.update(Queries.Q_QUOTE_DETAILS_REMOVE_BY_DETAIL_ID_3, namedParameters);*/
		
		/*
		//WORKS FINE
		return jdbcTemplate.batchUpdate(
			Queries.Q_QUOTE_DETAILS_REMOVE_BY_DETAIL_ID,
			new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int qdbId = lqdbForDelete[i];
				ps.setInt(1, qdbId);
			}
			public int getBatchSize() {
				return lqdbForDelete.length;
			}
		});*/
	}

	@Override
	public boolean removeQuoteDetailsByQuoteId(Integer quoteId) {
		boolean bin = false;
		int result = jdbcTemplate.update(Queries.Q_QUOTE_DETAILS_REMOVE_BY_QUOTE_ID, quoteId);
		if (result > 0) {
            bin = true;
        }
		return bin;
	}

}
